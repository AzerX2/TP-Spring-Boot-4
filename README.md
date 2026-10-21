# TP - Architecture Hexagonale avec Spring Boot 4
## Réponses aux Questions

1. Pourquoi le domaine n'importe-t-il ni Spring ni JPA ? Que gagne-t-on concrètement pour les tests ?
On a vu que Le domaine n'importe rien de technique ; l'application n'importe ni adaptateur ni Spring car les Les dépendances pointent vers l'intérieur

2. Pourquoi le service applicatif n'a-t-il ni `@Service` ni `@Transactional` ? Qui ouvre et ferme les transactions ?
car le service applicatif doit rester du java pur

3. Pourquoi garder deux classes distinctes `Coffee` et `CoffeeJpaEntity` au lieu d'annoter directement le domaine ?
Pour permettre de séparer les responsabilités 

4. La règle « prix entre 0 et 200 » est vérifiée dans le domaine et dans une contrainte SQL, mais pas dans le DTO. Pourquoi ?
ya pas besoin de le modif dans le dto normalement tu as besoin de changer seulement dans le domaine

5. Pourquoi un prix de 500 donne-t-il un 422 alors qu'un nom vide donne un 400 ?
c'est pas la meme erreur, si le nom est vide alors c'est un probleme de requete

6. Que se passerait-il sans `@EntityGraph` sur le repository ? Comment l'observer dans les logs ?
Ce serait beaucoup moins optimisée donc potentiellement des soucis de performance 

7. Quels fichiers faudrait-il modifier pour remplacer H2 par PostgreSQL ? Le domaine et l'application changent-ils ?
pom.xml : remplacer H2 par org.postgresql:postgresql.
et application.properties / application-dev.properties
et sur la db
