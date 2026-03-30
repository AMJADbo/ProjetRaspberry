-- phpMyAdmin SQL Dump
-- version 4.5.4.1
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Lun 30 Mars 2026 à 13:37
-- Version du serveur :  5.7.11
-- Version de PHP :  5.6.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `rfid_payment`
--

-- --------------------------------------------------------

--
-- Structure de la table `badges`
--

CREATE TABLE `badges` (
  `id` bigint(20) NOT NULL,
  `actif` bit(1) NOT NULL,
  `date_association` datetime(6) NOT NULL,
  `uid_rfid` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Contenu de la table `badges`
--

INSERT INTO `badges` (`id`, `actif`, `date_association`, `uid_rfid`, `user_id`) VALUES
(1, b'1', '2026-03-19 00:00:00.000000', '84af25d9d7', 2),
(2, b'1', '2026-03-19 00:00:00.000000', 'df1393bfe', 1),
(3, b'1', '2026-03-19 00:00:00.000000', '4065b3a432', 3);

-- --------------------------------------------------------

--
-- Structure de la table `transactions`
--

CREATE TABLE `transactions` (
  `id` bigint(20) NOT NULL,
  `date` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `montant` decimal(38,2) NOT NULL,
  `type` enum('CREDIT','PAYMENT') NOT NULL,
  `merchant_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Contenu de la table `transactions`
--

INSERT INTO `transactions` (`id`, `date`, `description`, `montant`, `type`, `merchant_id`, `user_id`) VALUES
(1, '2026-03-30 15:24:04.284442', 'Rechargement du compte', '10.00', 'CREDIT', NULL, 2),
(2, '2026-03-30 15:30:38.589464', 'Rechargement du compte', '1.00', 'CREDIT', NULL, 2);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `actif` bit(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `role` enum('ADMIN','MERCHANT','USER') NOT NULL,
  `solde` decimal(38,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Contenu de la table `users`
--

INSERT INTO `users` (`id`, `actif`, `email`, `mot_de_passe`, `nom`, `role`, `solde`) VALUES
(1, b'1', 'admin@test.com', 'admin', 'Admin', 'ADMIN', '0.00'),
(2, b'1', 'user@test.com', 'user', 'Utilisateur Test', 'USER', '61.00'),
(3, b'1', 'merchant@test.com', 'user', 'Commercant Test', 'MERCHANT', '0.00'),
(4, b'1', 'thierno@test', 'thierno', 'CISSE Thierno', 'MERCHANT', '0.00');

--
-- Index pour les tables exportées
--

--
-- Index pour la table `badges`
--
ALTER TABLE `badges`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKjwgs0ramvag1k433bhmutnnut` (`uid_rfid`),
  ADD KEY `FK6xty094p6yob3kcweag7yv6c8` (`user_id`);

--
-- Index pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKstlk10r5lac32uh71ypycltf0` (`merchant_id`),
  ADD KEY `FKqwv7rmvc8va8rep7piikrojds` (`user_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `badges`
--
ALTER TABLE `badges`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT pour la table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `badges`
--
ALTER TABLE `badges`
  ADD CONSTRAINT `FK6xty094p6yob3kcweag7yv6c8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Contraintes pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `FKqwv7rmvc8va8rep7piikrojds` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKstlk10r5lac32uh71ypycltf0` FOREIGN KEY (`merchant_id`) REFERENCES `users` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
