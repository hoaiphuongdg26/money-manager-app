<?php
require "DataBase.php";
$db = new DataBase();

if (isset($_POST['fullname']) && isset($_POST['UserName']) && isset($_POST['Password'])) {
    $fullname = $_POST['fullname'];
    $UserName = $_POST['UserName'];
    $Password = $_POST['Password'];

    if ($db->dbConnect()) {
        if ($db->signUp("user_information", $fullname, $UserName, $Password)) {
            echo "Registration Successful. You can now login.";
        } 
        else {
            echo "Registration failed. Please try again.";
        }
    } else {
        echo "Error: Database connection";
    }
else {
    echo "All fields are required ";
}
?>
