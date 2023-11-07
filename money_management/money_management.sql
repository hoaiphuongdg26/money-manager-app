-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Nov 07, 2023 at 06:43 AM
-- Server version: 5.7.40
-- PHP Version: 8.1.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `money_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `user_information`
--

DROP TABLE IF EXISTS `user_information`;
CREATE TABLE IF NOT EXISTS `user_information` (
  `UserID` int(11) NOT NULL AUTO_INCREMENT,
  `FullName` varchar(50) NOT NULL,
  `UserName` varchar(50) NOT NULL,
  `Password` varchar(50) NOT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `PhoneNumber` varchar(15) DEFAULT NULL,
  `Avatar` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `UserName` (`UserName`,`Email`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_information`
--

INSERT INTO `user_information` (`UserID`, `FullName`, `UserName`, `Password`, `Email`, `PhoneNumber`, `Avatar`) VALUES
(1, '', 'admin', '21232f297a57a5a743894a0e4a801fc3', NULL, NULL, NULL),
(3, 'Đinh Văn Trường Giang', 'Giang', '827ccb0eea8a706c4c34a16891f84e7b', 'truonggiangnsl123@gmail.com', '0382383930', NULL);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
