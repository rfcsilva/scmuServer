package pt.agroSmart.util;

public class Strings {

    public static final long DAY_IN_MS = 1000 * 60 * 60 * 24;


    //Initial msgs
    public static final String ABOUT_TO_LOGIN =  "Attempt to login user: ";
    public static final String DELETE_EMAILS_MESSAGE = "O GAE vai eliminar os tokens expirados";

    //OK messages
    public static final String USER_REGISTED = "User registered ";


    //Register Errors
    public static final String PARAMETER_ERROR = "Missing or wrong parameter.";
    public static final String FAILED_REQUIERED_PARAMS = "ERROR: Requiered field have not been correctly inserted.";
    public static final String FAILED_PHONE = "ERROR: Telephone number has not been correctly inserted.";
    public static final String FAILED_MOBILEPHONE = "ERROR: Mobile Phone number has not been correctly inserted.";
    public static final String FAILED_CC = "ERROR: User CC number has not been correctly inserted.";
    public static final String FAILED_NIF = "ERROR: User NIF has not been correctly inserted.";
    public static final String FAILED_POSTALCODE = "ERROR. User address has not been correctly inserted.";
    public static final String ALREADY_EXISTS = "ERROR: User already exists.";


    //user_ops_errors
    public static final String NOT_VALID_TOKEN = "ERROR: The authentication token is no longer valid. redirecting to login page...";
    public static final String CANT_CHANGE_VALUES = "Impossible to change those fields";

    //Roles
    public static final String BASE_USER = "user";
    public static final String VOLUNTEER = "volunteer";
    public static final String ENTITY = "entity";
    public static final String GBO = "gbo";
    public static final String GS = "gs";
    public static final String OBE = "obe";


    //user field constants
    public static final String USERNAME = "username";
    public static final String USER_NAME = "user_name";
    public static final String PASSWORD = "user_pwd";
    public static final String EMAIL = "user_email";
    public static final String MOBILE_PHONE = "mobile_phone";
    public static final String FIRST_ADDRESS = "first_address";
    public static final String COMP_ADDRESS = "complementary_address";
    public static final String LOCALITY = "locality";
    public static final String POSTAl_CODE = "postalcode";
    public static final String USER_CREATION = "user_creation_time";
    public static final String NUMB_REPOSRTS = "numb_reports";
    public static final String APROVAL_RATE = "approval_rate";
    public static final String ROLE = "role";
    public static final long INITIAL = 0L;
    public static final double INITIAL_FLOAT = 0.0;
    public static final String SOLVED_CASES = "solved_cases";


    //token

    public static final long EXPIRATION_TIME = DAY_IN_MS*2; //2 days
    public static final String AUTHENTICATION = "AUTHENTICATION";


    //markers strings
    public static final String MARKER = "marker";
    public static final String MARKER_NAME = "marker_name";
    public static final String MARKER_OWNER = "marker_owner";
    public static final String MARKER_DESCRIPTION = "marker_description";
    public static final String MARKER_COORDINATES = "marker_coordinates";
    public static final String MARKER_RISK = "marker_risk";
    public static final String MARKER_LATITUDE = "marker_latitude";
    public static final String MARKER_LONGITUDE = "marker_longitude";
    public static final String MARKER_ID = "marker_key";
    public static final String MARKER_STATUS = "marker_status";
    public static final String MARKER_LIKES = "marker_likes";
    public static final String MARKER_COMMENT = "marker_comment";
    public static final String MARKER_LIKERS = "marker_likers";

    //markers errors
    public static final String MARKER_NOTFOUND = "Marker not found.";



    public static final int STATS_LIST_SIZE = 8;

    public static final int PAGE_SIZE = 15;
    public static final String CURSOR = "Cursor";
    public static final String UNCONFIRMED_USERS = "unconfirmed_users";
    public static final String DELETE_GHOST_USERS = "Deleting ghost users";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String MAP_LIST_ALL_CURSOR = "Curor_List_All_Markers";
    public static final String MAP_LIST_MY_MARKERS_CURSOR = "Curor_List_My_Markers";
    public static final String LIKE = "like";


    //Login Resource Constants

    public static final String WRONG_PASSWORD = "Wrong password for username: ";

    public static final String MARKER_COMMENTS_KIND = "marker_comments_kind";
    public static final String COMMENT_CREATION = "comment_creation";

    public static final String COMMENT_ID = "comment_id";
    public static final String COMMENT_AUHTOR = "comment_author";


    public static final String TOTAL_DATASTORE_ENTITIES = "total_datastore_entities";
    public static final String TOTAL_DATASTORE_USED_SPACE = "total_datastore_used_space";
    public static final String FIRST_CURSOR = "startquery";
    public static final String MARKER_CREATION = "marker_creation";
    public static final int HASH_LENGTH = 9;
    public static final String CANT_CREATE_EVENTS = "Only entity users can create events.";

    //events
    public static final String EVENT = "event";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_ALERT = "event_alert";
    public static final String EVENT_LOCATION = "event_location";
    public static final String EVENT_AREA = "event_area";
    public static final String EVENT_DESCRIPTION = "event_description";
    public static final String EVENT_CREATOR = "event_creator";
    public static final String EVENT_CREATION_DATE = "event_creation_date";
    public static final String MEETUP_DATE = "event_meetup_date";
    public static final String EVENT_END_DATE = "event_end_date";
    public static final String IMAGE_URI = "image_uri";
    public static final String EVENT_INTEREST = "Event_Interest";
    public static final String USER_INTEREST = "user_interest";
    public static final String EVENT_CONFIRMATION = "event_confirmation";
    public static final String USER_CONFIRMATION = "user_confirmation";
    public static final String EVENT_ADMIN = "event_admin";
    public static final Object INSNT_ADMIN = "User not admin";


    //group
    public static final String GROUP = "group";
    public static final String GROUP_NAME = "group_name";
    public static final String GROUP_POINTS = "group_points";
    public static final String GROUP_PRIVACY = "group_privacy";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_ADMIN = "group_admin";
    public static final String GROUP_BASE = "group_base_users";
    public static final String GROUP_CREATION = "group_creation_date";
    public static final String MARKER_DISTRICT = "marker_district";
    public static final String MARKER_COUNTY = "marker_county";
    public static final String MARKER_PARISH = "marker_parish";


    public static final String MARKER_TYPE_TRASH = "trash";
    public static final String MARKER_TYPE_FIRE = "fire";
    public static final String MARKER_TYPE_BONFIRE ="bonfire";
    public static final String MARKER_TYPE_DIRTY_WOODS ="dirty_woods";
    public static final String MARKER_TYPE = "marker_type";
    public static final String GROUP_REQUEST = "group_request";


    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";


    public static final String VOLUNTEER_INVITES = "volunteer_invites";


    public static final String GROUP_PUBLICATION = "group_publish";


    public static final String PUBLICATION_ID = "publication_id";
    public static final String AUTHOR = "publication_author";
    public static final String PUBLICATION_MESSAGE = "publication_message";
    public static final String PUBLICATION_LIKES = "publication_likes";


    public static final String PUBLICATION_LIKE = "publication_like";


    public static final String PUBLICATION_COMMENT = "publication_comment";


    public static final String INVITATIONS = "Invitations";


    public static final String GROUP_MEMBER = "Group_Member";


    public static final String GROUP_NOT_FOUND = "Group Not Found.";


    public static final String PUBLICATION_CREATION = "publication_creation";


    public static final String LAST_MARKER = "last_marker";


    public static final String CONFIRMATION_CODE = "confirmation_code";


    public static final String TOKEN = "token";


    public static final String GROUP_COUNT = "group_count";


    public static final String NUMB_MEMBERS = "numb_members";


    public static final String GROUP_DISTRICT = "group_district";


    public static final String RADIOUS = "radious";


    public static final String INVITE_ID = "invite_id";


    public static final String CENTER = "center_point";


    public static final String MEETUP_POINT = "meetup_point";




    public static final String REGISTER_CODE = "register_code";


    public static final String IS_ADMIN = "isAdmin";
    public static final String IS_BASE_USER = "isBaseUser";


    public static final String USER_PROFILE_PICTURE = "user picture";


    public static final String NEW_PICTURE = "new_picture";












}
