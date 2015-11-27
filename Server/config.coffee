cfenv = require("cfenv")
class Config
    configArray : []
    constructor : (@configEnvironment)->
        appEnv  = cfenv.getAppEnv()
        @configArray = {}
        mongodbCon = "mongodb://IbmCloud_14qj6ifb_lh5nhj6e_9sidusb0:JEa8bOz6xhhdoGKgGmbXIW6dhFqqZzv4@ds047940.mongolab.com:47940/IbmCloud_14qj6ifb_lh5nhj6e"
        if @configEnvironment is "online"
            @configArray = 
                port : appEnv.port || 80
                mongodb : mongodbCon
        else if @configEnvironment is "local"
            @configArray = 
                port : appEnv.port || 80 
                mongodb : mongodbCon
                #mongodb : "mongodb://127.0.0.1:27017/sia"
        @configArray['client_id'] = "H5RAGRWGNHHMK1UGXIAAC0AYP4Y3KQXSZZOKDZGJLZIMPHNV"
        @configArray['client_secret'] = "EPSSWTRTV13CEB4VE4WGXVVZHKZQJBUTHO0ZRSGXARZWWBZV"
        @configArray['google_key'] = "AIzaSyBmZiA9-ISpaiC7XYapPJPVvqziBwWdmgE";

        @configArray['google_nearby'] = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=#{@configArray['google_key']}";
        @configArray['google_image'] = "https://maps.googleapis.com/maps/api/place/photo?maxheight=200&key=#{@configArray['google_key']}"
        @configArray['blacklisted_types'] = ["establishment","point_of_interest"]
        @configArray['my_ip'] = "http://192.168.1.3:4004"
module.exports = Config
