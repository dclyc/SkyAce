express = require('express')
request = require("request")
#cfenv = require("cfenv")
Config = require("./config")
mongodb = require("mongodb").MongoClient
Trip = require("./routes/trip")
User = require("./routes/user")
Place = require("./routes/place")
Review = require("./routes/review")

bodyParser = require("body-parser")
ObjectID  = require("mongodb").ObjectID
q = require("q")
cloudinary = require "cloudinary"
multer = require "multer"
busboy = require('connect-busboy')

class App
    Models : {}
    CmdOptions : []
    constructor : ()->
        @mode = "local"
        @router = new express()
        process.argv.forEach (val,index,array)=>
            @CmdOptions.push(val)
        @router.use express.static(__dirname+ '/public')
        @router.use bodyParser.urlencoded({extended : true})
        @router.use bodyParser.json {limit : '50mb'}
        @router.use (req, res, next)=>
            res.setHeader "Access-Control-Allow-Origin", "*"
            res.setHeader "Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, OPTIONS, DELETE"
            res.setHeader "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept"
            res.setHeader "Access-Control-Allow-Credentials", true

            next()
        @mUpload = multer({dest : "uploads/"})
        @cloudinary = cloudinary
        @cloudinary.config {cloud_name : "doai36lfy", api_key : "497541891563819", api_secret : "tp8ZWGBX2hHeDtrqJmZIm3s38fA"}
        @config = new Config(@mode)['configArray']
        @router.listen @config.port, ()=>
            console.log("Server starting "+@config.port);
        @router.get "/hello" , (req,res)=>
            return @sendContent res, "Hello I'm running coffee"
        @router.get "/services", (req, res)=>
            return @sendContent res, @config.mongodb
        @request = request
        @q = q
        @initRoutesAndDB()
    objectID : (id) =>
        return new ObjectID(id)
    initRoutesAndDB : ()=>
        console.log @config.mongodb
        mongodb.connect @config.mongodb, (err,db)=>
            if !err
                @Models.entities = db.collection("entities")
                @Models.trip = db.collection("trip")
                @Models.user = db.collection("user")
                @Models.review = db.collection "recentreviews"
                @Models.place = db.collection "place"
                @Models.tags = db.collection "tags"
                @Models.location = db.collection "location"
                @Models.saved = db.collection "saved"

                console.log "Connection to mongodb"
                tripRoute = new Trip @
                userRoute = new User @
                placeRoute = new Place @
                reviewRoute = new Review @
            else
               console.log "Error #{err}"
    getUserFromAT : (at)=>
        defer = @q.defer();
        @Models.user.findOne {access_token: at}, (err,result)=>
            if !err and result
                defer.resolve result

        return defer.promise
    sendContent : (res, content)=>
        res.status 200
        return res.json content

    sendErrorCode : (res, errorCode, content)=>
        res.status errorCode
        return res.end content
new App()
module.exports = App
