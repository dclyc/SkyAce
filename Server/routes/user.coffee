uuid = require("node-uuid")
class User
    constructor : (@App)->
        @App.router.post "/location/user", @setLocation
        @App.router.post "/register", @register
        @App.router.get "/check/:user", @checkUserExists
        @App.router.get "/user/photos", @getPhotoByUser

     getPhotoByUser : (req, res)=>
       user = @App.getUserFromAT(req.query.access_id)
       @App.Models.place.find({"photo.user._id" : user['_id']}).toArray (err, doc)=> #Find photo
         if !err and doc
           return @App.sendContent res, doc
    checkUserExists : (req, res)=>
        @App.Models.user.findOne {userid : req.params.user }, (err,doc)=>
            if !err
                if doc
                    return @App.sendContent res, doc["access_token"]
                else
                    return @App.sendContent res, "NOT_A_USER"
        #return @App.sendContent res, "NOT_A_USER"
    register : (req, res)=>
        random = uuid.v4().split("-").join ""
        @App.Models.user.insert {userid : req.body.userid, username : req.body.username, interests : req.body.interests, country : req.body.country,access_token : random}, (err,result)=>
            if !err and result
                return @App.sendContent res, result["ops"][0]["access_token"]
    setLocation : (req,res)=>
        @App.Models.user.update {username : req.body.username}, {$set : {location : req.body.lat}}, (err,body)=>
            if !err and body
                return @App.sendContent res, body

module.exports = User
