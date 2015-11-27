http = require('follow-redirects').https
multer = require "multer"
moment = require "moment"
fs = require "fs"
class Place
    constructor : (@App)->
        @App.router.post "/place", @createPlace
        @App.router.get "/place/:placeid", @getPlaceById
        @App.router.get "/place", @getNearbyPlacesGoogle
        @App.router.get "/estimate", @estimateUserLocation
        @App.router.get "/search", @search
        @App.router.get "/imageref/:photo", @imageref
        @App.router.get "/save/:placeid", @savePlace
        @App.router.get "/saved", @getPlacesForUser
        @App.router.get "/saved/remove/:placeid", @removePlacesForUser
        @App.router.get "/tags" , @getPlaceTags
        @App.router.post "/place/photo/:placeid", @App.mUpload.any(), @postUploadImage
        @App.router.get "/place/photos/:placeid", @getPhotoForPlace
    getPhotoForPlace : (req, res)=>
        @App.Models.place.findOne {_id : @App.objectID(req.params.placeid)}, (err, result)=>
            if !err
                if result
                    photos = result['photo']
                    responsePhotos = []
                    if photos
                        for photo in photos
                            found = false

                            for photosWithSameDate in responsePhotos
                                for photoWithSameDate in photosWithSameDate
                                    if photoWithSameDate['date_taken'] == photo['date_taken']
                                        photosWithSameDate.push photo
                                        found = true
                                        break
                            if !found
                                newPhotoWithSameDate = []
                                newPhotoWithSameDate.push photo

                                responsePhotos.push newPhotoWithSameDate

                    return @App.sendContent res, responsePhotos
                else
                    return @App.sendErrorCode res, 404, "Empty"
            else
                return @App.sendErrorCode res, 500, err
    postUploadImage : (req, res)=>
        user = @App.getUserFromAT(req.query.access_id)
        @App.cloudinary.uploader.upload req.files[0].path, (result)=>
            user.then (user)=>
                console.log user
                photoEntity =
                    url : result['url']
                    date_taken : moment().startOf('day').unix()
                    user : user
                console.log "Uplaoded"
                @App.Models.place.update {_id : @App.objectID(req.params.placeid) }, {$push : {photo : photoEntity }}, (err,doc)=>
                    if err
                        console.log "error"
                    else
                        console.log doc  
        , {
            crop: 'fill',
            width: 150,
            height: 150}
        
        return @App.sendContent res, "IMAGE_UPLOAD_SUCCESS"
    getPlaceTags : (req, res)=>
        @App.Models.tags.distinct "tag" , (err,result)=>
            if !err
                return @App.sendContent res, result
            else
                return @App.sendErrorCode req, res,500, err
    distance = (lat1, lon1, lat2, lon2, unit)=>
        radlat1 = Math.PI * lat1/180
        radlat2 = Math.PI * lat2/180
        radlon1 = Math.PI * lon1/180
        radlon2 = Math.PI * lon2/180
        theta = lon1-lon2
        radtheta = Math.PI * theta/180
        dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
        dist = Math.acos(dist)
        dist = dist * 180/Math.PI
        dist = dist * 60 * 1.1515
        if unit is "K"
            dist = dist * 1.609344
        if unit is "N"
            dist = dist * 0.8684
        return dist
    imageref : (req, res)=>
        res.redirect "#{@App.config.google_image}&photoreference=#{req.params.photo}"
    getPlacesForUser : (req, res)=>
        @App.getUserFromAT(req.query.access_id).then (user)=>
            console.log user
            console.log {"user.userid" : user['userid'] }
            @App.Models.saved.find({"user.userid" : user['userid'] }).toArray (err,result)=>
                console.log result
                console.log "get places"
                if !err and result
                    return @App.sendContent res, result.reverse()
    removePlacesForUser : (req, res)=>
        @App.getUserFromAT(req.query.access_id).then (user)=>
            @App.Models.saved.remove {_id : @App.objectID(req.params.placeid), "user.userid" : user['userid']}, (err, doc)=>
                if !err and doc
                    return @App.sendContent res, "DELETED"
                else
                    return @App.sendErrorCode res, 400, "SOMETHING_WENT_WRONG"
    savePlace : (req, res)=>
        @App.getUserFromAT(req.query.access_id).then (user)=>
            @App.Models.place.findOne {_id : @App.objectID(req.params.placeid)}, (err,place)=>
                console.log "Found place!"
                if !err and place
                    place['place_id'] = place['_id']
                    place['user'] = user
                    console.log place
                    @App.Models.saved.insert place , (err,result)=>
                        if !err and result
                            return @App.sendContent res, result

    getPlaceById : (req, res)=>
        @App.Models.place.findOne {_id : @App.objectID(req.params.placeid)},{ photo : 0},(err, result)=>
            if !err
                if result
                    return @App.sendContent res, result
                else
                    return @App.sendErrorCode res, 404, "Empty"
            else
                return @App.sendErrorCode res, 500, err
    insertIfNeeded : (location ,req)=>
        defer = @App.q.defer();

        location['distance'] = distance location['geometry']['location']['lat'], location['geometry']['location']['lng'], req.query.lat, req.query.lng, "K"
        place =
            name : location["name"]
            address : location["vicinity"]
            location:
                type : "Point"
                coordinates : [location["geometry"]["location"]['lng'], location["geometry"]["location"]["lat"]]
            place_id : location['place_id']
            tags : location["types"]
            photos : []
            reviews : []

        if location['photos']
            for photo in location['photos']
                place['photos'].push photo
        @App.Models.place.findOne {place_id: place['place_id']}, (err, result)=>
            if !err
                if !result #Not in db yet
                    @App.Models.place.insert place, (err,result)=>
                        if !err and result
                            _return = result["ops"][0]
                            _return["distance"] = location["distance"]
                            _return['new'] = true
                            defer.resolve _return
                else
                    result["distance"] = location["distance"]
                    result["new"] = false
                    defer.resolve result
        return defer.promise
    estimateUserLocation : (req, res)=>
        @App.Models.place.find({location : {$near : { $geometry : {type : "Point", coordinates : [parseFloat(req.query.lng), parseFloat(req.query.lat)]}}}}).toArray (err, result)=>
            if !err and result
                return @App.sendContent res, result[0]
    getNearbyPlacesGoogle : (req, res)=>
        additional = ""
        if req.query.tags.length > 0
            additional += "&types="
            req.query.tags.forEach (tag, index)=>
                if index isnt 0
                    additional += "|#{tag}"
                else
                    additional += "#{tag}"

        @App.request @App.config.google_nearby+"&radius=500&location=#{req.query.lat},#{req.query.lng}"+additional, (err, response,body)=>
            if !err and response and body
                locations = JSON.parse(body)['results']
                types = []
                locations.forEach (location ,index)=>
                    @App.config.blacklisted_types.forEach (blacklisted_type)=>
                        blacklisted_index = location['types'].indexOf blacklisted_type
                        if(blacklisted_index != -1 )
                            location['types'].splice blacklisted_index , 1
                            types.push location['types']

                for typeInArray in types
                    for type in typeInArray
                        @App.Models.tags.insert {tag : type},{multi: true}, (err,result)=>
                            if err
                                console.log err

                    #@App.config.interested_tags.forEach (tag, tagIndex)=>
                    #    if (location['types'].indexOf(tag) isnt -1)
                    #        locations.splice index, 1

                locations = locations.sort (item1, item2)=>
                    item1Dist = distance item1['geometry']['location']['lat'] , item1['geometry']['location']['lng'], req.query.lat, req.query.lng, "K"
                    item2Dist = distance item2['geometry']['location']['lat'] , item2['geometry']['location']['lng'], req.query.lat, req.query.lng, "K"

                    item1['distance'] = item1Dist
                    item2['distance'] = item2Dist
                    return parseFloat(item1Dist) - parseFloat(item2Dist)
                promises = []
                for location in locations
                    promises.push @insertIfNeeded location, req
                @App.getUserFromAT(req.query.access_id).then (user)=>
                    @App.Models.saved.find({"user.userid": user['userid']}).toArray (err,saved)=>
                        savedPlaceIds = []
                        for save in saved
                            savedPlaceIds.push save['place_id'].toString()
                        console.log savedPlaceIds
                        @App.q.allSettled(promises).then (results)=>
                            places = []
                            results.forEach (result)=>
                                place = result["value"]
                                if savedPlaceIds.indexOf(place['_id'].toString()) > -1
                                    place['isSaved'] = true
                                else
                                    place['isSaved'] = false      
                                places.push place

                            return @App.sendContent res, places
    #If lat and lng specified, calls getNearbyPlacesGoogle , will find locations to visit via google places api
    search : (req, res)=>
        query = {}
        if req.query.name
            query['name'] = new RegExp ".*#{req.query.name}.*" , 'i'
        #if req.query.tags
        #    query['tags'] = {$in : req.query.tags.split(",")}
        #Airport or hotel location

        if req.query.lat and req.query.lng
            query['location'] =
                $near :
                    $geometry :
                        type : "Point",
                        coordinates : [parseFloat(req.query.lng), parseFloat(req.query.lat)]
            console.log req.query
            @App.getUserFromAT(req.query.access_id).then (user)=>
                req.query.tags = user['interests']
                return @getNearbyPlacesGoogle req, res


    createPlace : (req, res)=>
        place =
            name : req.body.name
            description : req.body.description
            address : req.body.address
            location :
                lat : req.body.lat
                lng : req.body.lng

        @App.Models.place.insert place , (err, result)=>
            if !err
                return @App.sendContent res, result['ops'][0]
            else
                return @App.sendErrorCode res, 500, err
module.exports = Place
