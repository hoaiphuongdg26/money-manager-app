<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['yourName']) && isset($_POST['UserName']) && isset($_POST['Password']) ) {
    $yourName = $_POST['yourName'];
    $username = $_POST['UserName'];
    $password = $_POST['Password'];

    if ($db->dbConnect()) {
        if ($db->checkExistingUser("user_information", $username)) {
            echo "Username already exists. Please choose a different username.";
        } else {
            if ($db->registerUser("user_information", $yourName, $username, $password)) {
                echo "Registration Successful. You can now login.";
            } else {
                echo "Registration failed. Please try again.";
            }
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required ";
}
?>