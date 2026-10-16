-- =====================================================================
-- Seed des catégories / sous-catégories de réclamation (HexaClientSpace)
-- Idempotent : INSERT IGNORE + contraintes UNIQUE sur "code".
-- Peut être rejoué à chaque démarrage sans créer de doublons.
-- =====================================================================

-- ---------- Catégories ----------
INSERT IGNORE INTO categories (code, libelle, description, ordre, actif) VALUES
 ('WIFI',        'Problème avec le WIFI (Routeur, Flybox...)', 'Connexion internet fixe, routeur, Flybox, WIFI domestique', 1, 1),
 ('WIFI_MOBILE', 'Problème avec le WIFI mobile (4G / 5G)',     'Connexion data mobile 4G ou 5G',                            2, 1),
 ('FORFAIT',     'Problème avec un forfait',                   'Changement, consommation, options et renouvellement',       3, 1),
 ('PAIEMENT',    'Problème avec un paiement',                  'Facturation, débit, remboursement',                         4, 1),
 ('AUTRE',       'Autre problème',                             'Toute demande ne rentrant pas dans les catégories ci-dessus', 5, 1);

-- ---------- WIFI ----------
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'WIFI_ROUTEUR_HS', 'Le routeur ne s''allume pas ou clignote anormalement', 1, 1, id FROM categories WHERE code = 'WIFI';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'WIFI_SIGNAL_FAIBLE', 'Signal WIFI faible ou instable', 2, 1, id FROM categories WHERE code = 'WIFI';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'WIFI_SANS_INTERNET', 'Connecté au WIFI mais pas d''accès à internet', 3, 1, id FROM categories WHERE code = 'WIFI';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'WIFI_FLYBOX', 'Problème avec la Flybox', 4, 1, id FROM categories WHERE code = 'WIFI';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'WIFI_CONFIG', 'Changer le nom ou le mot de passe du WIFI', 5, 1, id FROM categories WHERE code = 'WIFI';

-- ---------- WIFI mobile ----------
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'MOBILE_4G', 'Problème de connexion 4G', 1, 1, id FROM categories WHERE code = 'WIFI_MOBILE';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'MOBILE_5G', 'Problème de connexion 5G', 2, 1, id FROM categories WHERE code = 'WIFI_MOBILE';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'MOBILE_DEBIT_LENT', 'Débit mobile lent', 3, 1, id FROM categories WHERE code = 'WIFI_MOBILE';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'MOBILE_SANS_RESEAU', 'Aucun réseau mobile', 4, 1, id FROM categories WHERE code = 'WIFI_MOBILE';

-- ---------- Forfait ----------
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'FORFAIT_CHANGEMENT', 'Changer de forfait', 1, 1, id FROM categories WHERE code = 'FORFAIT';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'FORFAIT_CONSOMMATION', 'Consommation ou volume de data incorrect', 2, 1, id FROM categories WHERE code = 'FORFAIT';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'FORFAIT_OPTION', 'Activation ou désactivation d''une option', 3, 1, id FROM categories WHERE code = 'FORFAIT';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'FORFAIT_RENOUVELLEMENT', 'Renouvellement ou résiliation', 4, 1, id FROM categories WHERE code = 'FORFAIT';

-- ---------- Paiement ----------
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'PAIEMENT_DOUBLE_DEBIT', 'Débité deux fois', 1, 1, id FROM categories WHERE code = 'PAIEMENT';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'PAIEMENT_FACTURE_ERRONEE', 'Facture erronée', 2, 1, id FROM categories WHERE code = 'PAIEMENT';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'PAIEMENT_ECHEC', 'Paiement refusé ou échoué', 3, 1, id FROM categories WHERE code = 'PAIEMENT';
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'PAIEMENT_REMBOURSEMENT', 'Demande de remboursement', 4, 1, id FROM categories WHERE code = 'PAIEMENT';

-- ---------- Autre ----------
INSERT IGNORE INTO sous_categories (code, libelle, ordre, actif, categorie_id)
SELECT 'AUTRE_DEMANDE', 'Autre demande', 1, 1, id FROM categories WHERE code = 'AUTRE';

-- =====================================================================
-- Techniciens (numéros fictifs). Idempotent grâce à l'unicité du téléphone.
-- =====================================================================
INSERT IGNORE INTO techniciens (nom, prenom, telephone, niveau_experience, actif) VALUES
 ('Ben Salah', 'Mohamed', '20111001', 5, 1),
 ('Jebali',    'Karim',   '21111002', 4, 1),
 ('Gharbi',    'Sonia',   '22111003', 3, 1),
 ('Mansouri',  'Walid',   '50111004', 2, 1),
 ('Chebbi',    'Rania',   '52111005', 4, 1),
 ('Haddad',    'Anis',    '55111006', 1, 1),
 ('Bouaziz',   'Ines',    '95111007', 3, 1),
 ('Khelifi',   'Slim',    '27547896', 5, 1),
 ('Mhidi',     'Arbi',    '55420609', 2, 1),
 ('Hosni',     'Farid',   '98745600', 1, 0),
 ('Jomaaa',    'Nomeene', '32549004', 1, 1);

 -- =====================================================================
-- Hibernate 6 crée en général la colonne messages_ia.role en ENUM('USER','ASSISTANT') sur MySQL,
-- et ddl-auto=update n'y ajoute pas la valeur SUPPORT. On la convertit en VARCHAR (idempotent).
-- =====================================================================
ALTER TABLE messages_ia MODIFY role VARCHAR(15) NOT NULL;
 