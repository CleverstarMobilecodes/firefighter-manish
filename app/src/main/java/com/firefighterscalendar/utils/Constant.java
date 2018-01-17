package com.firefighterscalendar.utils;


public interface Constant {

    String DEVICE_TYPE = "Android";
    String APPVERSION = "1";
    String SERVER_KEY = "AIzaSyDbqI8-YZ7M6xQ6YBSpjJxaQDHDFMBAWCU";
    String SENDER_ID = "574287737148";

    String BASE_URL = "http://firefightersapp.com.au/api/v1/api/";
//    String BASE_URL = "http://dev2.spaceo.in/project/firefighter/code/api/v1/api/";

    String URL_SIGNUP = BASE_URL + "signup";
    String URL_LOGIN = BASE_URL + "login";
    String URL_EDIT_PROFILE = BASE_URL + "editprofile";
    String URL_CHANGE_PASSWORD = BASE_URL + "changepassword";
    String URL_GET_ALL_ALBUMS = BASE_URL + "getallalbums";
    String URL_GET_ALL_ALBUM_IMAGES = BASE_URL + "getallalbumimages";
    String URL_GET_ALL_CATEGORIES = BASE_URL + "getallcategories";
    String URL_GET_ALL_PRODUCTS = BASE_URL + "getallproducts";
    String URL_GET_PRODUCT_DETAILS = BASE_URL + "getproductdetails";
    String URL_USER_SUBSCRIPTION = BASE_URL + "usersubscription";
    String URL_CHECK_USER_SUBSCRIPTION = BASE_URL + "checkusersubscription";
    String URL_ADD_USER_ORDER = BASE_URL + "adduserorder";
    String URL_ADD_USER_EVENT = BASE_URL + "adduserevent";
    String URL_UPDATE_USER_EVENT = BASE_URL + "updateuserevent";
    String URL_DELETE_USER_EVENT = BASE_URL + "deleteuserevent";
    String URL_GET_PAGE = BASE_URL + "getpage";
    String URL_GET_CALENDAR_IMAGE = BASE_URL + "getcalendarimage";
    String URL_GET_USER_EVENTS_DAY = BASE_URL + "getusereventsday";
    String URL_GET_USER_EVENTS_MONTH = BASE_URL + "getusereventsmonth";
    String URL_ADD_PRODUCT_INTO_CART = BASE_URL + "addproductintocart";
    String URL_DISPLAY_USER_CART = BASE_URL + "displayusercart";
    String URL_REMOVE_PRODUCT_FROM_CART = BASE_URL + "removeproductfromcart";
    String URL_GET_USER_ORDERS = BASE_URL + "getuserorders";
    String URL_GET_DETAIL_USER_ORDER = BASE_URL + "getdetailuserorder";
    String URL_FORGOT_PASSWORD = BASE_URL + "forgotpassword";
    String URL_GET_ALL_NEWS = BASE_URL + "getallnews";
    String URL_GET_SHIPPING_ADDRESS = BASE_URL + "getshippingaddress";
    String URL_GET_NEWS_DETAILS = BASE_URL + "getnewsdetails";
    String URL_UPDATE_NOTIFICATION = BASE_URL + "usernotification";
    String URL_USER_GREETINGS_AND_CALANDER = BASE_URL + "usergritingsandcalander";
    String URL_SIGNUP_WITHOUT_DEVICE = BASE_URL + "signupwithdevicetoken";
    String URL_UPDATE_USER_SUBSCRIPTION = BASE_URL + "updateusersubscription";
}