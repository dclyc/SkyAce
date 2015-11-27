class Trip
    constructor : (@App)->
        @App.router.post "/trip/start", @startTrip
        @App.router.post "/trip/end", @endTrip 
        @App.router.get "/trip/:tripid", @getTripById
        @App.router.get "/trip/user/:username", @getTripsByUser
        @App.router.get "/trip", @getRecentTrips
    getRecentTrips : (req, res)=>
        @App.Models.trip.find({started : true, ended : true}).toArray (err, result)=>
            if !err
                return @App.sendContent res, result
            else
                return @App.sendErrorCode res, 500, err
    getTripById : (req, res)=>
        @App.Models.trip.findOne {_id : @App.objectID(req.params.tripid)}, (err,result)=>
            return @App.sendContent res, result
    getTripsByUser : (req, res)=>
        @App.Models.trip.find({'user.name' : (req.params.username)}).toArray (err, result)=>
            return @App.sendContent res, result
    startTrip : (req, res)=>
        trip = 
            name : req.body.name
            ended : false
        @App.Models.user.findOne {_id : @App.objectID(req.body.user_id)}, (err, result)=>
            trip['user'] = result 
            if !err
                @App.Models.trip.insert trip, (err, result)=>
                    return @App.sendContent res, result["ops"][0]
            else
                return @App.sendErrorCode res, 500, err 
    endTrip : (req, res)=>
        @App.Models.trip.findOne {_id : @App.objectID(req.body.trip_id )}, (err, result)=>
            if !err
                if result['user_id'] is req.body.user_id
                    return @App.sendErrorCode res, 400, "Trip does not belong to this user!"
                if result['ended'] is true
                    return @App.sendErrorCode res, 400, "Trip has already ended"
                @App.Models.trip.update {_id : @App.objectID(result['_id'])}, { $set : {started: true, ended: true}}, (err, result)=>
                    if !err
                        return @App.sendContent res, result
                    else
                        return @App.sendErrorCode res, 500, err
            else
                return @App.sendErrorCode res, 500, err
    
module.exports = Trip