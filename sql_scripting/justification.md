### Justification du choix du modèle en étoile

Dans le cadre de notre projet, nous avons choisi d'adopter un modèle en **étoile** pour transformer notre table actuelle en un modèle multidimensionnel. Voici les raisons principales qui justifient ce choix :

1. **Simplicité et performance**  
   Le modèle en étoile est reconnu pour sa simplicité et son efficacité. Il se compose d'une table de faits centrale (contenant les mesures) et de plusieurs tables de dimensions (contenant les attributs des données). Cette structure rend les requêtes plus simples et plus rapides, car elle limite le nombre de jointures nécessaires.

2. **Optimisation des requêtes**  
   En raison de sa conception, le modèle en étoile permet des requêtes plus performantes, car il n'y a généralement qu'une jointure entre la table des faits et les tables de dimensions. Cela est particulièrement adapté pour des analyses rapides et efficaces, ce qui est essentiel dans le contexte de l'analyse de données volumineuses.

3. **Adaptation aux outils de BI**  
   Le modèle en étoile est largement utilisé dans les outils de Business Intelligence tels que Tableau ou PowerBI. Ces outils sont conçus pour exploiter efficacement ce type de structure de données afin de produire des visualisations et des rapports analytiques.

4. **Facilité d'évolution**  
   Le modèle en étoile permet d'ajouter facilement de nouvelles dimensions ou mesures sans perturber l'architecture de la base de données. Cette évolutivité est un atout majeur pour les projets à long terme, où les besoins en termes d'analyse de données peuvent évoluer.

### Technologies utilisées

Pour la mise en œuvre de ce modèle en étoile, nous avons utilisé les technologies suivantes :

- **PostgreSQL** : gestion des bases de données et optimisation des requêtes multidimensionnelles.
- **Python avec Jupyter** : analyse et manipulation des données avant leur intégration dans le Data Warehouse.
- **Docker** : orchestration des services et gestion des environnements de développement.
- **Beekeeper** : gestion simplifiée des bases de données PostgreSQL.

Ces technologies facilitent la gestion et l'analyse des données, et leur combinaison permet une mise en œuvre efficace du modèle en étoile tout en garantissant la performance et la flexibilité du système.

### Explication des dimensions

Le modèle en étoile que nous avons conçu comprend cinq dimensions principales. Voici une description détaillée de chacune d'elles :

1. **dim_datetime**  
   Cette dimension regroupe toutes les informations relatives aux dates. Elle permet d'analyser les données selon différents niveaux de temporalité, tels que l'année, le mois, le jour, l'heure, etc. Elle facilite ainsi l'analyse des tendances temporelles et des comportements saisonniers des données.

2. **dim_location**  
   Bien que cette dimension existe, elle n'est pas utilisée pour le moment dans notre modèle. Elle serait destinée à contenir des informations géographiques ou de localisation, telles que les villes, régions, ou autres points de vente, et pourrait être utilisée pour des analyses géographiques à l'avenir.

3. **dim_payment_type**  
   Cette dimension regroupe les différents types de paiement utilisés dans les transactions. Elle permet de segmenter les données en fonction des méthodes de paiement, telles que les cartes de crédit, les paiements mobiles, ou les espèces. Cela peut être utile pour analyser les préférences des clients en matière de paiement.

4. **dim_ratecode**  
   Cette dimension contient des informations sur les prix ou les tarifs appliqués aux services ou produits. Elle permet de catégoriser les données selon les différents niveaux de prix ou les types de tarification, facilitant ainsi les analyses sur les variations des prix et leur impact sur les ventes ou les revenus.

5. **dim_vendor**  
   Cette dimension regroupe les informations sur les vendeurs ou les fournisseurs. Elle permet d'analyser les données en fonction des différents acteurs commerciaux, facilitant ainsi la comparaison entre les performances des différents vendeurs et l'identification des tendances ou des anomalies associées à chaque fournisseur.

Chaque dimension a été conçue pour fournir une vue d'ensemble complète des données, facilitant ainsi leur analyse sous différents angles et permettant de répondre à diverses questions commerciales ou analytiques.

### Justification des IDs

Les identifiants sont des éléments essentiels dans un modèle en étoile, car ils permettent de lier les différentes dimensions à la table de faits. Chaque dimension est identifiée par un clé primaire unique, et cette clé est utilisée dans la table des faits pour relier les mesures aux dimensions. Voici la justification pour chaque ID utilisé dans notre modèle :

1. **trip_id**  
   L'identifiant **trip_id** est utilisé pour identifier de manière unique chaque trajet dans la table des faits. C'est la clé primaire dans la table des faits et elle permet de lier toutes les informations relatives à un trajet spécifique, y compris les dimensions de localisation, de paiement, de tarification, et de vendeur.

2. **vendor_id**  
   L'identifiant **vendor_id** fait référence à l'identifiant unique du vendeur ou du fournisseur dans la dimension **dim_vendor**. Il permet de relier les informations concernant chaque vendeur (par exemple, un taxi spécifique ou un fournisseur de service) aux trajets. Cela permet de réaliser des analyses sur la performance de chaque vendeur, comme le nombre de trajets réalisés ou le revenu généré par chaque vendeur.

3. **pickup_datetime_id**  
   L'identifiant **pickup_datetime_id** est utilisé pour lier chaque trajet à une date et heure spécifiques dans la dimension **dim_datetime**. Cela permet de connaître la date et l'heure de la prise en charge du trajet. L'ID permet d'analyser les tendances temporelles des trajets, telles que l'impact de l'heure de la journée ou du jour de la semaine sur la demande.

4. **ratecode_id**  
   L'identifiant **ratecode_id** fait référence à la dimension **dim_ratecode** qui contient des informations sur les prix ou les types de tarification associés à chaque trajet. Cet ID permet de lier un trajet particulier à son code de tarif, ce qui est utile pour analyser la variation des prix et comprendre l'impact des différentes stratégies tarifaires.

5. **pickup_location_id**  
   L'identifiant **pickup_location_id** fait référence à la dimension **dim_location**, bien qu'elle ne soit pas utilisée actuellement. Cet ID serait utilisé pour relier le trajet à un lieu spécifique de prise en charge. Une fois activée, cette dimension permettrait de réaliser des analyses géographiques sur les points de départ des trajets et de repérer les zones les plus sollicitées.

6. **dropoff_location_id**  
   L'identifiant **dropoff_location_id** fait référence à la dimension **dim_location** également, mais pour le lieu de dépôt. Cela permettrait de lier un trajet à sa destination géographique, et comme pour le lieu de prise en charge, cela permettrait des analyses géographiques sur les zones de destination les plus fréquentes.

7. **payment_type**  
   L'identifiant **payment_type** se réfère à la dimension **dim_payment_type**, qui contient les informations sur les types de paiement utilisés pour chaque trajet (par exemple, carte de crédit, espèces, paiement mobile). Cet ID permet de suivre les préférences de paiement des clients et d'analyser comment le mode de paiement affecte les ventes ou les comportements des clients.

### Résumé des liens entre la table des faits et les dimensions

- La table des faits contient des références aux identifiants des dimensions via des clés étrangères.  
- **trip_id** relie la table des faits à chaque trajet unique.  
- **vendor_id**, **pickup_datetime_id**, **ratecode_id**, **pickup_location_id**, **dropoff_location_id**, et **payment_type** sont utilisés pour relier chaque trajet aux différentes dimensions correspondantes.  
- Ces liens permettent des analyses multidimensionnelles efficaces, telles que l'analyse des tendances temporelles, des comportements géographiques, des types de paiement, et des stratégies tarifaires.
