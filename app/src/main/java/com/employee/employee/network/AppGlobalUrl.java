package com.employee.employee.network;

public class AppGlobalUrl {

    private static String BaseUrl = "http://jmdapplication.ml/api/";

    /*Post request*/
    public static String Login = BaseUrl + "sign-in.php";
    public static String Register = BaseUrl + "sign-up.php";
    public static String user_details_update = BaseUrl + "user-details-update.php";
    public static String generate_repo = BaseUrl + "generate-repo.php";
    public static String put_image = BaseUrl + "put-images.php";
    public static String put_claimed_amount = BaseUrl + "put-amount.php";
    public static String search_car = BaseUrl + "search-car.php";
    public static String total_page = BaseUrl + "/total-page.php";




    /*Get request*/

    public static String get_user_details = BaseUrl + "user-details.php?user_id=";
    public static String get_car_list = BaseUrl + "get-carlists.php?page=";
    public static String get_single_car_info = BaseUrl + "get-single-carinfo.php?car_id=";

}
