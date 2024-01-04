-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Jan 04, 2024 at 07:55 AM
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
  `ID` varchar(36) NOT NULL,
  `UserID` int(11) DEFAULT NULL,
  `CategoryID` varchar(36) DEFAULT NULL,
  `Note` varchar(150) DEFAULT NULL,
  `TimeCreate` datetime DEFAULT NULL,
  `Expense` double DEFAULT NULL,
  `sync_status` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `bill`
--

INSERT INTO `bill` (`ID`, `UserID`, `CategoryID`, `Note`, `TimeCreate`, `Expense`, `sync_status`) VALUES
('6039b84c-e931-4fe4-8c2c-0d9efd72aba3', 6, '0d42f809-be1a-4e7c-9c3d-94fcd6775746', 'Unnamed Bill', '2024-01-04 14:51:42', -10000, NULL),
('9cc94193-b4e3-4287-943c-629f6590f1c5', 6, '0d42f809-be1a-4e7c-9c3d-94fcd6775746', 'user2', '2024-01-04 14:45:33', -333, NULL),
('9e8ba497-a21e-4caa-acee-5e0930b89899', 3, '6c25235d-f69e-46ba-ad9c-80246ea454ce', 'ar', '2024-01-04 14:43:09', -12344321, NULL),
('d5662464-f732-4736-b719-afe2925b858b', 3, '6a0993cf-99bf-4466-ad05-125cbe69920c', 'fo', '2024-01-04 14:43:30', -12, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
CREATE TABLE IF NOT EXISTS `category` (
  `ID` varchar(36) NOT NULL,
  `UserID` int(11) NOT NULL,
  `Name` varchar(150) NOT NULL,
  `Icon` varchar(150) NOT NULL,
  `Color` varchar(150) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`ID`, `UserID`, `Name`, `Icon`, `Color`) VALUES
('0d42f809-be1a-4e7c-9c3d-94fcd6775746', 6, 'Car', 'ic_car', 'colorbutton_3'),
('6a0993cf-99bf-4466-ad05-125cbe69920c', 3, 'Food', 'ic_food', 'colorbutton_2'),
('6c25235d-f69e-46ba-ad9c-80246ea454ce', 3, 'Car', 'ic_car', 'colorbutton_3');

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
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_information`
--

INSERT INTO `user_information` (`UserID`, `FullName`, `UserName`, `Password`, `Email`, `PhoneNumber`, `Avatar`) VALUES
(1, '', 'admin', '21232f297a57a5a743894a0e4a801fc3', NULL, NULL, NULL),
(3, 'Đinh Văn Trường Giang', 'Giang', '827ccb0eea8a706c4c34a16891f84e7b', 'truonggiangnsl123@gmail.com', '0382383930', NULL),
(6, 'iuHH', 'hp', '202cb962ac59075b964b07152d234b70', NULL, NULL, NULL);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
