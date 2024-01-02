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
    $name = isset($_POST['name']) ? mysqli_real_escape_string($con, $_POST['name']) : '';
//    $icon = isset($_POST['icon']) ? mysqli_real_escape_string($con, $_POST['icon']) : '';
    $color = isset($_POST['color']) ? mysqli_real_escape_string($con, $_POST['color']) : '';

    // Insert data into the database
    $sql = "INSERT INTO category (Name, Color) 
            VALUES ('$name', '$color')";

    if (mysqli_query($con, $sql)) {
        $status = 'OK';
        $query = "SELECT * FROM category";
        $result = mysqli_query($con, $query);

        if ($result) {
            $data = array();
            while ($row = mysqli_fetch_assoc($result)) {
                $data[] = $row;
            }
            // Return JSON response with the retrieved data
            echo json_encode(array("response" => "OK", "data" => $data));
        }else {
            $status = 'FAILED: ' . mysqli_error($con);
            // Return JSON response
            echo json_encode(array("response" => $status));
        }
    }else {
        $status = 'FAILED: ' . mysqli_error($con);
        // Return JSON response
        echo json_encode(array("response" => $status));
    }

} 
else {
    //If the method is GET
    if($_SERVER['REQUEST_METHOD'] == 'GET'){
        // Retrieve all data from the "category" table
        $query = "SELECT * FROM category";
        $result = mysqli_query($con, $query);

        if ($result) {
            $data = array();

            // Fetch data and store in an array
            while ($row = mysqli_fetch_assoc($result)) {
                $data[] = $row;
            }
            // Return JSON response with the retrieved data
            echo json_encode(array("response" => "OK", "categorydata" => $data));
        } else {
            $status = 'FAILED: ' . mysqli_error($con);
            // Return JSON response
            echo json_encode(array("response" => $status));
        }
    }
}
// Close connection
mysqli_close($con);
?>
