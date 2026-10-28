-- ============================================================
-- SCRIPT SQL COMPLET - LegalAssist Madagascar AI
-- ============================================================
-- À exécuter dans PostgreSQL (via pgAdmin ou psql) AVANT de
-- lancer l'application pour la première fois.
--
-- Étapes :
--   1) Crée la base :  CREATE DATABASE legalassist_db;
--   2) Connecte-toi dessus :  \c legalassist_db
--   3) Exécute tout ce fichier.
-- ============================================================

-- ------------------------------------------------------------
-- Table utilisateur
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS utilisateur (
    id                  SERIAL PRIMARY KEY,
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    login               VARCHAR(50)  NOT NULL UNIQUE,
    mot_de_passe_hash   VARCHAR(255) NOT NULL,
    role                VARCHAR(20)  NOT NULL CHECK (role IN ('AVOCAT', 'JUGE', 'ADMIN'))
);

-- ------------------------------------------------------------
-- Table dossier
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dossier (
    id                    SERIAL PRIMARY KEY,
    numero                VARCHAR(50) NOT NULL UNIQUE,
    titre                 VARCHAR(255) NOT NULL,
    description           TEXT,
    categorie             VARCHAR(100),
    date_creation         DATE NOT NULL DEFAULT CURRENT_DATE,
    statut                VARCHAR(20) NOT NULL DEFAULT 'EN_COURS' CHECK (statut IN ('EN_COURS', 'CLOTURE', 'ARCHIVE')),
    avocat_responsable    INTEGER REFERENCES utilisateur(id)
);

-- ------------------------------------------------------------
-- Table loi
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS loi (
    id                  SERIAL PRIMARY KEY,
    nom                 VARCHAR(255) NOT NULL,
    domaine             VARCHAR(100) NOT NULL,
    date_publication    DATE,
    source              VARCHAR(255)
);

-- ------------------------------------------------------------
-- Table article_loi
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS article_loi (
    id                SERIAL PRIMARY KEY,
    loi_id            INTEGER NOT NULL REFERENCES loi(id) ON DELETE CASCADE,
    numero_article    VARCHAR(50) NOT NULL,
    contenu           TEXT NOT NULL,
    mots_cles         VARCHAR(500) -- mots séparés par des virgules, ex: "terrain,propriete,litige"
);

-- ------------------------------------------------------------
-- Table audience
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audience (
    id              SERIAL PRIMARY KEY,
    dossier_id      INTEGER NOT NULL REFERENCES dossier(id) ON DELETE CASCADE,
    date_audience   DATE,
    lieu            VARCHAR(255),
    demandeur       VARCHAR(255),
    defendeur       VARCHAR(255),
    notes           TEXT
);

-- ------------------------------------------------------------
-- Table document
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS document (
    id                SERIAL PRIMARY KEY,
    dossier_id        INTEGER NOT NULL REFERENCES dossier(id) ON DELETE CASCADE,
    nom_fichier       VARCHAR(255) NOT NULL,
    chemin_fichier    VARCHAR(500) NOT NULL,
    type              VARCHAR(20),
    date_ajout        DATE NOT NULL DEFAULT CURRENT_DATE
);

-- ------------------------------------------------------------
-- Table mot_cle_ia
-- ------------------------------------------------------------
-- Stocke de façon PERMANENTE les mots-clés appris par l'IA
-- (via l'écran "Entraînement IA"), pour qu'ils ne soient plus
-- perdus à la fermeture de l'application.
CREATE TABLE IF NOT EXISTS mot_cle_ia (
    id          SERIAL PRIMARY KEY,
    categorie   VARCHAR(100) NOT NULL,
    mot_cle     VARCHAR(100) NOT NULL,
    UNIQUE (categorie, mot_cle)
);

-- ============================================================
-- DONNÉES D'EXEMPLE
-- ============================================================

-- Utilisateur administrateur par défaut
-- Login : admin / Mot de passe : admin123 (à changer !)
--
-- IMPORTANT : BCrypt génère un "sel" aléatoire différent à chaque
-- exécution, donc AUCUN hash fixe ne peut être collé ici à l'avance.
-- Avant d'exécuter ce script :
--   1) Lance la classe com.legalassist.utils.GenerateurMotDePasse
--      (clic droit -> Run File dans NetBeans/IntelliJ)
--   2) Copie le hash affiché dans la console
--   3) Remplace <HASH_A_COLLER_ICI> ci-dessous par ce hash
--   4) Puis exécute ce script SQL
INSERT INTO utilisateur (nom, prenom, login, mot_de_passe_hash, role)
VALUES ('RAKOTO', 'Admin', 'admin', '<HASH_A_COLLER_ICI>', 'ADMIN')
ON CONFLICT (login) DO NOTHING;

-- Quelques lois d'exemple
INSERT INTO loi (nom, domaine, date_publication, source) VALUES
('Code civil malagasy - Livre foncier', 'Droit foncier', '1960-08-09', 'Journal Officiel de Madagascar'),
('Code pénal malagasy', 'Droit pénal', '1962-11-20', 'Journal Officiel de Madagascar'),
('Code du travail malagasy', 'Droit du travail', '2003-09-17', 'Journal Officiel de Madagascar'),
('Code de procédure civile', 'Procédure judiciaire', '1961-06-01', 'Journal Officiel de Madagascar');

-- Articles de loi d'exemple avec mots-clés pour la recherche IA
INSERT INTO article_loi (loi_id, numero_article, contenu, mots_cles) VALUES
(1, 'Art. 34', 'Tout occupant d''un terrain de plus de 10 ans peut demander un titre foncier par prescription acquisitive.', 'terrain,propriete,titre foncier,occupation,possession'),
(1, 'Art. 12', 'Toute personne réclamant la propriété d''un terrain doit produire un titre ou une preuve d''occupation légale.', 'terrain,propriete,litige,parcelle,cadastre'),
(2, 'Art. 305', 'Le vol est le fait de soustraire frauduleusement la chose d''autrui.', 'vol,plainte,delit'),
(2, 'Art. 309', 'Les coups et blessures volontaires sont punis selon la gravité du préjudice causé.', 'coups,blessures,agression'),
(3, 'Art. 67', 'Le licenciement doit être justifié par une cause réelle et sérieuse, sous peine d''être jugé abusif.', 'licenciement,employeur,salarie,contrat de travail'),
(3, 'Art. 71', 'Tout salarié licencié a droit à un préavis dont la durée dépend de son ancienneté.', 'licenciement,preavis,salarie,demission'),
(4, 'Art. 15', 'Toute assignation devant le tribunal doit être notifiée dans un délai raisonnable avant l''audience.', 'tribunal,audience,assignation,convocation');

-- ============================================================
-- FIN DU SCRIPT
-- ============================================================
