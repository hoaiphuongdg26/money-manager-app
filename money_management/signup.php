<?php
$UserName = $_POST["UserName"];
$Password = $_POST["Password"];
$FullName = $_POST["Name"];

require 'DataBase.php';
if($con){
    $sql = "select * from user_information where UserName = '$UserName'";
    $result = mysqli_query($con,$sql);

    if(mysqli_num_rows($result)>0){
        $status = "ok";
        $result_code = 0;
        echo json_encode(array('status'=>$status,'result_code'=>$result_code));
    }
    else{
        //$Password = md5($Password);
        $sql = "insert into user_information(FullName,UserName,Password) values('$FullName','$UserName','$Password')";
        if(mysqli_query($con,$sql)){
            $status = "ok";
            $result_code = 1;

            // Get the user ID of the newly inserted user
            $user_id = mysqli_insert_id($con);
            $id1 = bin2hex(random_bytes(16));
            $id2 = bin2hex(random_bytes(16));
            $id3 = bin2hex(random_bytes(16));
            $id4 = bin2hex(random_bytes(16));
            $id5 = bin2hex(random_bytes(16));
            // Insert default category for the user
            $sql_insert_category1 = "INSERT INTO category (ID, UserID, Name, Icon, Color) VALUES ('$id1','$user_id', 'Electricity bill','ic_power','colorbutton_0')";
            $sql_insert_category2 = "INSERT INTO category (ID, UserID, Name, Icon, Color) VALUES ('$id2','$user_id', 'Food','ic_food','colorbutton_1')";
            $sql_insert_category3 = "INSERT INTO category (ID, UserID, Name, Icon, Color) VALUES ('$id3','$user_id', 'Wifi','ic_wifi','colorbutton_2')";
            $sql_insert_category4 = "INSERT INTO category (ID, UserID, Name, Icon, Color) VALUES ('$id4','$user_id', 'Game','ic_game','colorbutton_3')";
            $sql_insert_category5 = "INSERT INTO category (ID, UserID, Name, Icon, Color) VALUES ('$id5','$user_id', 'Shopping','ic_shopping_cart','colorbutton_4')";
            if(mysqli_query($con, $sql_insert_category1) &&
                mysqli_query($con, $sql_insert_category2) &&
                mysqli_query($con, $sql_insert_category3) &&
                mysqli_query($con, $sql_insert_category4) &&
                mysqli_query($con, $sql_insert_category5)){
                $status = "ok";
                $result_code = 1;
                echo json_encode(array('status'=>$status,'result_code'=>$result_code));
            }
            else{
                $status = "failed";
                echo json_encode(array('status'=>$status), JSON_FORCE_OBJECT);
            }
        }
        else{
            $status = "failed";
            echo json_encode(array('status'=>$status),JSON_FORCE_OBJECT);
        }
    }
}
else{
    $status = "failed";
            echo json_encode(array('status'=>$status),JSON_FORCE_OBJECT);
}
?>