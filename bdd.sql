CREATE DATABASE covoiturage_db;
USE covoiturage_db;

-- Table des utilisateurs
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom_complet VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    photo_profil VARCHAR(255),
    ville VARCHAR(50),
    type_utilisateur ENUM('conducteur', 'passager'),
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP,
    notification_push BOOLEAN,
    notification_sms BOOLEAN
);
ALTER TABLE users ADD COLUMN ville VARCHAR(50);

-- Table des véhicules
CREATE TABLE vehicles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    marque VARCHAR(50) NOT NULL,
    modele VARCHAR(50) NOT NULL,
    annee INT,
    plaque_immatriculation VARCHAR(20),
    nombre_places INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table des trajets
CREATE TABLE trips (
    id INT PRIMARY KEY AUTO_INCREMENT,
    conducteur_id INT,
    vehicle_id INT,
    lieu_depart VARCHAR(255) NOT NULL,
    lieu_arrivee VARCHAR(255) NOT NULL,
    date_depart DATETIME NOT NULL,
    prix_par_place DECIMAL(8,2) NOT NULL,
    places_disponibles INT NOT NULL,
    places_total INT NOT NULL,
    statut ENUM('planifie', 'en_cours', 'termine', 'annule') DEFAULT 'planifie',
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conducteur_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Table des réservations
CREATE TABLE reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT,
    passager_id INT,
    nombre_places INT NOT NULL,
    statut ENUM('en_attente', 'confirme', 'refuse', 'annule') DEFAULT 'en_attente',
    date_reservation DATETIME DEFAULT CURRENT_TIMESTAMP,
    prix_total DECIMAL(8,2),
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (passager_id) REFERENCES users(id)
);

-- -- Table des messages
-- CREATE TABLE messages (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     trip_id INT,
--     expediteur_id INT,
--     destinataire_id INT,
--     contenu TEXT NOT NULL,
--     date_envoi DATETIME DEFAULT CURRENT_TIMESTAMP,
--     is_lu BOOLEAN DEFAULT FALSE,
--     FOREIGN KEY (expediteur_id) REFERENCES users(id),
--     FOREIGN KEY (destinataire_id) REFERENCES users(id),
--     FOREIGN KEY (trip_id) REFERENCES trips(id)
-- );

-- Table des avis
CREATE TABLE reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT,
    evaluateur_id INT,
    evalue_id INT,
    note INT CHECK (note >= 1 AND note <= 5),
    commentaire TEXT,
    date_evaluation DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(id),
    FOREIGN KEY (evaluateur_id) REFERENCES users(id),
    FOREIGN KEY (evalue_id) REFERENCES users(id)
);


-- Utilisateur Conducteur de test
INSERT INTO users (nom_complet, email, telephone, password_hash, type_utilisateur, notification_push, notification_sms) 
VALUES ('Ahmed Benali', 'ahmed.conducteur@email.com', '+213 555123456', 'password123', 'conducteur', true, true);

INSERT INTO users (nom_complet, email, telephone, password_hash, type_utilisateur, notification_push, notification_sms) 
VALUES ('Ahmed Benali', 'conducteur@conducteur.com', '+213 555123456', 'test', 'conducteur', true, true);

-- Utilisateur Passager de test
INSERT INTO users (nom_complet, email, telephone, password_hash, type_utilisateur, notification_push, notification_sms) 
VALUES ('Fatima Zohra', 'passager@passager.com', '+213 555654321', 'test', 'passager', true, false);