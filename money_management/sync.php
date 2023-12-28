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
    $note = isset($_POST['note']) ? mysqli_real_escape_string($con, $_POST['note']) : '';
    $timecreateString = isset($_POST['timecreate']) ? $_POST['timecreate'] : '';
    $expense = isset($_POST['expense']) ? $_POST['expense'] : '';
    $categoryID = isset($_POST['categoryID']) ? $_POST['categoryID'] : '';
    $userID = isset($_POST['userID']) ? $_POST['userID'] : '';

    // Convert the datetime string to a DateTime object
    $timecreate = DateTime::createFromFormat('Y-m-d H:i:s', $timecreateString);

    // Check if the conversion was successful
    if ($timecreate === false) {
        // Handle the error, perhaps log it
        $status = 'FAILED: Invalid datetime format';
    } else {
        // Format the DateTime object to the required format for MySQL DATETIME
        $formattedDatetime = $timecreate->format('Y-m-d H:i:s');

        // Insert data into the database
        $sql = "INSERT INTO bill (Note, TimeCreate, Expense, CategoryID, UserID) 
                VALUES ('$note', '$formattedDatetime', '$expense', '$categoryID', '$userID')";

        if (mysqli_query($con, $sql)) {
            $status = 'OK';
        } else {
            $status = 'FAILED: ' . mysqli_error($con);
        }
    }
} else {
    // No data sent, return an error
    $status = 'FAILED: No data received';
}

// Return JSON response
echo json_encode(array("response" => $status));

// Close connection
mysqli_close($con);
?>
