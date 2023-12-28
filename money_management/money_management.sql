-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Dec 28, 2023 at 12:53 AM
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
-- Table structure for table `bill`
--

DROP TABLE IF EXISTS `bill`;
CREATE TABLE IF NOT EXISTS `bill` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) DEFAULT NULL,
  `CategoryID` int(11) DEFAULT NULL,
  `Note` varchar(150) DEFAULT NULL,
  `TimeCreate` datetime DEFAULT NULL,
  `Expense` double DEFAULT NULL,
  `sync_status` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `bill`
--

INSERT INTO `bill` (`ID`, `UserID`, `CategoryID`, `Note`, `TimeCreate`, `Expense`, `sync_status`) VALUES
(5, 3, 1, 'not', '2023-12-28 00:00:00', 100000, NULL),
(6, 3, 1, 'notoff', '2023-12-28 00:00:00', 300000, NULL),
(7, 3, 1, 'notoff2', '2023-12-28 00:00:00', 300000, NULL),
(8, 3, 1, 'not28', '2023-12-28 00:00:00', 10000, NULL);

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
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_information`
--

INSERT INTO `user_information` (`UserID`, `FullName`, `UserName`, `Password`, `Email`, `PhoneNumber`, `Avatar`) VALUES
(1, '', 'admin', '21232f297a57a5a743894a0e4a801fc3', NULL, NULL, NULL),
(3, 'Đinh Văn Trường Giang', 'Giang', '827ccb0eea8a706c4c34a16891f84e7b', 'truonggiangnsl123@gmail.com', '0382383930', NULL),
(4, 'Test', 'test', '123', NULL, NULL, NULL),
(5, '', '', 'd41d8cd98f00b204e9800998ecf8427e', NULL, NULL, NULL);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
