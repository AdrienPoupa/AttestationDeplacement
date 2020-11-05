# Changelog

## [Non sorti]

## [3.2.1] - 2020-11-04
### Réglé
- Problème de date

## [3.2.0] - 2020-11-03
### Ajouté
- Sauvegarde des motifs
- Validation du formulaire
### Réglé
- Ajout de la description du motif "Enfants"
- Motifs dupliqués après un retour
### Changé
- Label des motifs

## [3.1.0] - 2020-11-01
### Changé
- Utilisation de la dernière version de l'attestation

## [3.0.1] - 2020-10-30
### Réglé
- QRcode généré conformément au formulaire officiel
- Affichage du QR code plus grand
- Fautes d'orthographes

## [3.0.0] - 2020-10-29
### Ajouté
- Attestation de déplacement dérogatoire : le retour de la vengeance

## [2.0.0] - 2020-10-20
### Ajouté
- Attestation de déplacement dérogatoire - Couvre Feu

## [1.7.0] - 2020-05-13
### Ajouté
- Déclaration de déplacement supérieur à 100km

## [1.6.0] - 2020-05-10
### Ajouté
- Auto-attestation pour les transports en Ile-de-France

## [1.5.0] - 2020-05-09
### Changé
- Amélioration de la liste des attestations

## [1.4.1] - 2020-04-29
### Réglé
- Affichage du fichier PDF sur Android <= 6

## [1.4.0] - 2020-04-17
### Réglé
- Format de l'adresse: rue, code postal, ville
- Amélioration du formulaire pour ne plus avoir de nouvelles lignes
### Changé
- Abbréviation des motifs
- Affichage du QR code plus grand
- Limitation du code postal à 5 caractères
### Ajouté
- Fenêtre à propos

## [1.3.0] - 2020-04-17
### Ajouté
- Date et heure de sortie
- Adresse, ville et code postal
- Confirmation de suppression d'attestation
### Réglé
- QRcode généré en UTF-8 conformément au formulaire officiel
### Changé
- Ajout de marges autour des motifs de sortie
- Méthode d'ouverture du PDF, ajout d'un message informatif si pas de lecteur PDF installé
- Création du PDF dans un thread, ajout d'une fenêtre de chargement

## [1.2.1] - 2020-04-11
### Changé
- Forcage des nombres pour le champ de date de naissance

## [1.2.0] - 2020-04-11
### Ajouté
- Ajout de l'application sur le Google Play Store
### Changé
- Remplacement du calendrier pour la date de naissance par un input manuel
- Renommage de l'application en "Attestation de déplacement"
### Réglé
- Support du dark mode de Android 10
- Correction QRCode pour le rendre identique à l'original

## [1.1.0] - 2020-04-08
### Changé
- Agrandissement du QR code
- Ajout du QR code au fichier PDF
- Utilisation du stockage interne, plus besoin de permissions
### Réglé
- Compatibilité PDF avec Android < 7

## [1.0.2] - 2020-04-07
### Réglé
- Motifs identiques au formulaire officiel
- Ajout du zéro devant les heures du QRCode
- Problème de permission sur Android 10
### Changé
- La sélection de la date de naissance commence par l'année

## [1.0.1] - 2020-04-06
### Réglé
- Format heure 24h
- Date de naissance ne fonctionnant pas pour Android <= 6
- Génération du PDF sur Android > 6: https://stackoverflow.com/a/41565209

## [1.0.0] - 2020-04-06
### Ajouté
- Première version