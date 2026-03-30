# RFID Payment System

Système de paiement sans contact par badge RFID avec Raspberry Pi.

## Prérequis

- Java 17+
- MySQL / XAMPP
- Maven
- Raspberry Pi avec lecteur RC522

## Installation rapide

### Linux / Raspberry Pi
```bash
git clone https://github.com/AMJADbo/ProjetRaspberry.git
cd rfid-payment
chmod +x install.sh
./install.sh
```

### Windows
```batch
git clone https://github.com/AMJADbo/ProjetRaspberry.git
cd rfid-payment
install.bat
```

## Accès

- Interface web : http://localhost:8080/login
- Rôles : ADMIN, USER, MERCHANT

## Module Raspberry Pi
```bash
cd RaspberryClient
javac RfidPayment.java
java RfidPayment
```

## Commande pour démarrer le serveur
```bash
 ./mvnw spring-boot:run
```


## !!!!! IMPORTANT !!!!! 
```bash
Il faut changer l'adresse ip en faisant ipconfig sur windows et changer l'ip d'API_URL dans le fichier RfidPayment qui est dans RaspberryClient.
```