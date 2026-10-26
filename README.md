# TP1 – Gestion de stock de pharmacie (Spring Boot + MySQL)

**Module :** Spring Boot – MicroServices – ENSA Fès
**Professeur :** Youness IDRISSI KHAMLICHI

Application de gestion du stock d'une pharmacie : entités JPA (OneToOne, OneToMany, ManyToOne, ManyToMany), base MySQL, affichage Thymeleaf et API REST testable avec Postman.

## Technologies
Java 17+, Spring Boot 3.3, Spring Data JPA, MySQL, Thymeleaf, Maven.

## Modèle de données

```
Categorie 1 ──── * Produit * ──── 1 Fournisseur
                     │ 1
                     │ 1
              DetailProduit
Produit * ──── * Tag   (table de jointure produit_tag)
```

| Relation | Annotation |
|---|---|
| Categorie → Produits | `@OneToMany(mappedBy="categorie")` / `@ManyToOne` côté Produit |
| Fournisseur → Produits | `@OneToMany(mappedBy="fournisseur")` / `@ManyToOne` côté Produit |
| Produit ↔ DetailProduit | `@OneToOne(cascade = ALL)` |
| Produit ↔ Tag | `@ManyToMany` + `@JoinTable(name="produit_tag")` |

## Structure

```
src/main/java/ma/ensa/pharmacie/
├── entity/        Categorie, Fournisseur, DetailProduit, Tag, Produit
├── repository/    JpaRepository pour chaque entité
├── controller/
│   ├── rest/      API REST (/api/...)
│   └── web/       Pages Thymeleaf
└── exception/     Gestion des erreurs REST (409, 400)
src/main/resources/
├── application.properties   (configuration MySQL)
├── data.sql                 (jeu de données du TP)
└── templates/               (vues Thymeleaf)
postman/                     collection Postman
```

## Lancer le projet

1. Démarrer MySQL (la base `pharmacie_db` est créée automatiquement).
2. Adapter `spring.datasource.username` / `password` dans `src/main/resources/application.properties`.
3. Lancer :
   ```bash
   mvn spring-boot:run
   ```
4. Ouvrir :
   - Pages web : http://localhost:8080/produits (aussi `/categories`, `/fournisseurs`, `/tags`)
   - API REST : http://localhost:8080/api/produits

Le jeu de données du TP (`data.sql`) est inséré au démarrage (`INSERT IGNORE`, rejouable sans doublons).

## API REST

| Méthode | URL | Statut |
|---|---|---|
| GET | `/api/produits` | 200 |
| GET | `/api/produits/{id}` | 200 / 404 |
| POST | `/api/produits` | 201 |
| PUT | `/api/produits/{id}` | 200 / 404 |
| DELETE | `/api/produits/{id}` | 204 / 404 |
| GET, POST, PUT, DELETE | `/api/categories`, `/api/fournisseurs`, `/api/tags` | idem |

Une référence inexistante (ex. `categorie.id` inconnu) ou la suppression d'un élément encore utilisé renvoie **409**.

## Tests Postman

Importer `postman/TP1-pharmacie.postman_collection.json` dans Postman et lancer la collection (Runner) : chaque requête vérifie le statut HTTP et le contenu JSON. Les requêtes POST enregistrent automatiquement les ids créés pour les tests suivants.

## Tests automatiques

```bash
mvn test
```
Les tests utilisent une base H2 en mémoire (aucun MySQL nécessaire) et vérifient les routes REST et les pages Thymeleaf.

## Captures d'écran
À ajouter : liste des produits, détail d'un produit, résultats Postman.
