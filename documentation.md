# Documentation du Microservice JRtools

Ce microservice a pour but de générer des rapports dynamiques au format PDF
à partir de modèles JasperReports.  
Il prend en entrée:

- Un template Jasper sous format `.jrxml` ou `.jasper`.
- Des paramètres sous format JSON.

Et retourne un