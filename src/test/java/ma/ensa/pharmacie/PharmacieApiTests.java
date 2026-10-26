package ma.ensa.pharmacie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PharmacieApiTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    org.springframework.jdbc.core.JdbcTemplate jdbc;

    @Test
    void listeProduitsContientLeJeuDeDonnees() throws Exception {
        MvcResult r = mvc.perform(get("/api/produits")).andExpect(status().isOk()).andReturn();
        JsonNode json = mapper.readTree(r.getResponse().getContentAsString());
        assertThat(json.size()).isGreaterThanOrEqualTo(5);
        JsonNode first = json.get(0);
        assertThat(first.get("categorie").get("nom").asText()).isEqualTo("Antibiotiques");
        assertThat(first.get("detailProduit").get("paysOrigine").asText()).isEqualTo("France");
        assertThat(first.get("tags").size()).isEqualTo(1);
    }

    @Test
    void annexes() throws Exception {
        mvc.perform(get("/api/categories")).andExpect(status().isOk());
        mvc.perform(get("/api/fournisseurs")).andExpect(status().isOk());
        mvc.perform(get("/api/tags")).andExpect(status().isOk());
        mvc.perform(get("/api/produits/9999")).andExpect(status().isNotFound());
    }

    @Test
    void cycleCrudProduit() throws Exception {
        // H2 n'avance pas son compteur auto-increment après des INSERT avec id explicite (MySQL, si) :
        // on le repositionne pour éviter les collisions avec le jeu de données.
        for (String table : new String[]{"categorie", "fournisseur", "produit", "detailProduit"}) {
            jdbc.execute("ALTER TABLE " + table + " ALTER COLUMN id RESTART WITH 100");
        }

        long catId = creerEtLireId("/api/categories",
                "{\"nom\":\"Anti-inflammatoires\",\"description\":\"Réduire l'inflammation\"}");
        long fourId = creerEtLireId("/api/fournisseurs",
                "{\"nom\":\"Novartis\",\"contact\":\"contact@novartis.com\",\"adresse\":\"Genève, Suisse\"}");

        String produit = """
                {"nom":"Voltaren 50mg","description":"Anti-inflammatoire","prix":58.90,
                 "codeBarre":"VOLTA50-2025","dateExpiration":"2026-12-15",
                 "categorie":{"id":%d},"fournisseur":{"id":%d},
                 "detailProduit":{"fabricant":"Novartis","paysOrigine":"Suisse","composition":"Diclofénac sodique 50 mg",
                                  "dosage":"50 mg","indications":"Douleurs musculaires"},
                 "tags":[{"id":1},{"id":3}]}
                """.formatted(catId, fourId);

        // POST -> 201
        MvcResult created = mvc.perform(post("/api/produits").contentType(MediaType.APPLICATION_JSON).content(produit))
                .andExpect(status().isCreated()).andReturn();
        JsonNode body = mapper.readTree(created.getResponse().getContentAsString());
        long id = body.get("id").asLong();
        assertThat(body.get("tags").size()).isEqualTo(2);
        long detailId = body.get("detailProduit").get("id").asLong();

        // GET -> 200
        mvc.perform(get("/api/produits/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Voltaren 50mg"));

        // PUT -> 200, le détail est modifié sur place (même id)
        String maj = produit.replace("Voltaren 50mg", "Voltaren 75mg").replace("58.90", "62.00")
                .replace("Suisse", "Allemagne");
        mvc.perform(put("/api/produits/" + id).contentType(MediaType.APPLICATION_JSON).content(maj))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Voltaren 75mg"))
                .andExpect(jsonPath("$.detailProduit.id").value(detailId))
                .andExpect(jsonPath("$.detailProduit.paysOrigine").value("Allemagne"));

        // DELETE -> 204 puis 404
        mvc.perform(delete("/api/produits/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/produits/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void categorieInexistanteDonne409() throws Exception {
        String produit = "{\"nom\":\"X\",\"prix\":1,\"categorie\":{\"id\":99999}}";
        mvc.perform(post("/api/produits").contentType(MediaType.APPLICATION_JSON).content(produit))
                .andExpect(status().isConflict());
    }

    @Test
    void pagesThymeleaf() throws Exception {
        mvc.perform(get("/produits")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Amoxicilline 500mg")));
        mvc.perform(get("/produits/1")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sanofi")));
        mvc.perform(get("/categories")).andExpect(status().isOk());
        mvc.perform(get("/fournisseurs")).andExpect(status().isOk());
        mvc.perform(get("/tags")).andExpect(status().isOk());
    }

    private long creerEtLireId(String url, String json) throws Exception {
        MvcResult r = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }
}
