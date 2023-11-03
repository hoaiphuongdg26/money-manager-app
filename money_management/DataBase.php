<?php
require "DataBaseConfig.php";

class DataBase
{
    public $connect;
    public $data;
    private $sql;
    protected $servername;
    protected $username;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $this->connect = null;
        $this->data = null;
        $this->sql = null;
        $dbc = new DataBaseConfig();
        $this->servername = $dbc->servername;
        $this->username = $dbc->username;
        $this->password = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    function dbConnect()
    {
        $this->connect = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        return $this->connect;
    }

    function prepareData($data)
    {
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }

    function logIn($table, $username, $password)
    {
        $username = $this->prepareData($username);
        $password =md5($this->prepareData($password));
        $this->sql = "select * from " . $table . " where UserName = '" . $username . "'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $dbusername = $row['UserName'];
            $dbpassword = $row['Password'];
            if ($dbusername == $username && $password== $dbpassword) {
                $login = true;
            } else $login = false;
        } else $login = false;

        return $login;
    }
    function signUp($table, $fullname, $UserName, $Password)
    {
        $fullname = $this->prepareData($fullname);
        $UserName = $this->prepareData($UserName);
        $Password = $this->prepareData($Password);
        $Password = password_hash($Password, PASSWORD_DEFAULT);
        $this->sql =
            "INSERT INTO " . $table . " (fullname, UserName, Password) VALUES ('" . $fullname . "','" . $UserName . "','" . $Password . "')";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else return false;
    }

}

?>