-- Création de la table user si elle n'existe pas
CREATE TABLE IF NOT EXISTS `user` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    mot_de_passe VARCHAR(255),
    telephone VARCHAR(255),
    role ENUM('ADMIN', 'DONATEUR', 'ORGANISATION'),
    enabled BOOLEAN DEFAULT true,
    account_non_expired BOOLEAN DEFAULT true,
    account_non_locked BOOLEAN DEFAULT true,
    credentials_non_expired BOOLEAN DEFAULT true
);

-- Création de la table organisation si elle n'existe pas
CREATE TABLE IF NOT EXISTS `organisation` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    numero_fiscal VARCHAR(20) NOT NULL,
    contact_principal VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    logo VARCHAR(255),
    description TEXT,
    statut_validation BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    admin_id INT,
    FOREIGN KEY (admin_id) REFERENCES `user`(id)
);

-- Création de la table action_charite si elle n'existe pas
CREATE TABLE IF NOT EXISTS `action_charite` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    date_debut DATE,
    date_fin DATE,
    lieu VARCHAR(255),
    objectif_collecte DOUBLE,
    somme_actuelle DOUBLE DEFAULT 0,
    categorie ENUM('EDUCATION', 'SANTE', 'ENVIRONNEMENT', 'AUTRE'),
    organisation_id BIGINT,
    FOREIGN KEY (organisation_id) REFERENCES `organisation`(id)
);

