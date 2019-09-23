<?php
if($_SERVER['HTTP_AUTHORIZATION'] != 'Basic YWJjOjEyMw==') header('WWW-Authenticate: Basic');
header('Content-Type: text/html');
echo 'Welcome!';
?>