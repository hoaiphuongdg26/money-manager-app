<?php
$user_name = "root";
$password = "";
$dbname = "money_management";
$host_name = "localhost";
$con = mysqli_connect($host_name, $user_name, $password, $dbname);

// Check connection
if (!$con) {
    die("Connection failed: " . mysqli_connect_error());
}

// Check if data is sent from the client
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Check if the variables are set
    $userID = isset($_POST['userID']) ? mysqli_real_escape_string($con, $_POST['userID']) : '';
    $fullName = isset($_POST['fullName']) ? $_POST['fullName'] : '';
    $userName = isset($_POST['userName']) ? $_POST['userName'] : '';
    //$email = isset($_POST['email']) ? $_POST['email'] : '';
    $_password = isset($_POST['_password']) ? $_POST['_password'] : '';
    $_password = md5($_password);
    //query update
    // Build the SQL UPDATE query
    $sql = "UPDATE user_information SET ";
    $sql .= "FullName = '$fullName', ";
    $sql .= "UserName = '$userName', ";
    $sql .= "Password = '$_password' ";
    //$sql .= "Email = '$email' ";
    $sql .= "WHERE UserID = $userID"; 

    // Execute the UPDATE query
    if (mysqli_query($con, $sql)) {
        $status = 'OK';
        // Return JSON response
        echo json_encode(array("response" => "OK"));
    } else {
        $status = 'FAILED: ' . mysqli_error($con);
        // Return JSON response
        echo json_encode(array("response" => $status));
    }
} else if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    // Retrieve all data from the "user_information" table
    $query = "SELECT * FROM user_information";
    $result = mysqli_query($con, $query);

    if ($result) {
        $data = array();

        // Fetch data and store in an array
        while ($row = mysqli_fetch_assoc($result)) {
            $data[] = $row;
        }
        // Return JSON response with the retrieved data
        echo json_encode(array("response" => "OK", "userdata" => $data));
    } else {
        $status = 'FAILED: ' . mysqli_error($con);
        // Return JSON response
        echo json_encode(array("response" => $status));
    }
}

// Close connection
mysqli_close($con);
?>
