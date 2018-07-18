-- phpMyAdmin SQL Dump
-- version 4.8.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 17, 2018 at 02:52 PM
-- Server version: 10.1.32-MariaDB
-- PHP Version: 7.2.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `java_messenger`
--

-- --------------------------------------------------------

--
-- Table structure for table `thread`
--

CREATE TABLE `thread` (
  `id` int(11) NOT NULL,
  `created` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `thread`
--

INSERT INTO `thread` (`id`, `created`) VALUES
(1, 1531736610),
(2, 1531739600),
(3, 1531739710),
(7, 1531740811),
(8, 1531830621);

-- --------------------------------------------------------

--
-- Table structure for table `thread_comment`
--

CREATE TABLE `thread_comment` (
  `id` int(11) NOT NULL,
  `thread_id` int(11) NOT NULL,
  `from_user_id` int(11) NOT NULL,
  `message` varchar(500) DEFAULT NULL,
  `created` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `thread_comment`
--

INSERT INTO `thread_comment` (`id`, `thread_id`, `from_user_id`, `message`, `created`) VALUES
(1, 1, 1, 'hello roscar', 1531737554),
(2, 1, 2, 'hello there too admin', 1531737616),
(3, 2, 1, 'hello roscar5', 1531739600),
(4, 3, 1, 'hello there caren', 1531739710),
(8, 7, 1, 'hello roscar3', 1531740811),
(9, 2, 1, 'hello roscar5 again', 1531740981),
(10, 1, 1, 'hello there again roscar from admin', 1531741572),
(11, 1, 1, 'hello there roscar', 1531742569),
(12, 8, 1, 'hello user1 this is admin', 1531830621),
(13, 8, 1, 'hello again user1. this is admin again', 1531830647),
(14, 8, 9, 'hello there admin this is user1', 1531830668);

-- --------------------------------------------------------

--
-- Table structure for table `thread_user`
--

CREATE TABLE `thread_user` (
  `id` int(11) NOT NULL,
  `thread_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `thread_user`
--

INSERT INTO `thread_user` (`id`, `thread_id`, `user_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 1),
(4, 2, 5),
(5, 3, 1),
(6, 3, 7),
(13, 7, 1),
(14, 7, 4),
(15, 8, 1),
(16, 8, 9);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`) VALUES
(1, 'admin'),
(2, 'roscar'),
(3, 'roscar2'),
(4, 'roscar3'),
(5, 'roscar5'),
(6, 'ross'),
(7, 'caren'),
(9, 'user1'),
(10, 'user2'),
(11, 'test');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `thread`
--
ALTER TABLE `thread`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `thread_comment`
--
ALTER TABLE `thread_comment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_thread_comment_thread1_idx` (`thread_id`),
  ADD KEY `fk_thread_comment_from_user1_idx` (`from_user_id`);

--
-- Indexes for table `thread_user`
--
ALTER TABLE `thread_user`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_thread_user_thread1_idx` (`thread_id`),
  ADD KEY `fk_thread_user_user1_idx` (`user_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `thread`
--
ALTER TABLE `thread`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `thread_comment`
--
ALTER TABLE `thread_comment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `thread_user`
--
ALTER TABLE `thread_user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `thread_comment`
--
ALTER TABLE `thread_comment`
  ADD CONSTRAINT `fk_thread_comment_from_user1` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `fk_thread_comment_thread1` FOREIGN KEY (`thread_id`) REFERENCES `thread` (`id`);

--
-- Constraints for table `thread_user`
--
ALTER TABLE `thread_user`
  ADD CONSTRAINT `fk_thread_user_thread1` FOREIGN KEY (`thread_id`) REFERENCES `thread` (`id`),
  ADD CONSTRAINT `fk_thread_user_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
