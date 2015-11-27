class Review 
    constructor : (@App)->
        @App.router.get "/review/user/:username", @getReviewForUser
        @App.router.post "/review/:place", @postReviewForPlace
        @App.router.get "/review/explore", @getRecentReviews

    getRecentReviews : (req, res)=>
        query = {}
        if req.query.tags 
            query['tags'] = req.query.tags

        @App.Models.review.find(query).toArray (err,result)=>
            if !err and result 
                return @App.sendContent res, result.reverse()
    #Create review for user
    postReviewForPlace : (req,res)=>
        validPlaceId = false
        if !req.query.access_id
            return @App.sendError res, 404, "No access token given!"
        @App.Models.place.findOne {_id : @App.objectID(req.params.place)}, (err,result)=>
            if !err 
                if result
                    validPlaceId = true 
                    user = @App.getUserFromAT(req.query.access_id)
                    user.then (userResult)=>
                        review = 
                            user : userResult
                            content : req.body.content
                        #Push review into reviews array for place
                        @App.Models.place.update {_id : @App.objectID(req.params.place)},{$push : {reviews : review}}, (err,insertedReview)=>
                            if !err and insertedReview
                                result['reviews'] = null
                                review['place'] = result
                                @App.Models.review.insert review, (err,result)=>
                                    if !err and result
                                        return @App.sendContent res, result
                else
                    return @App.sendError res, 404, "Invalid objectid"
            else
                return @App.sendError res, 400, err
    getReviewForUser : (req, res)=>
        @App.Models.review.find({username : req.params.username}).toArray (err,result)=>
            if !err and result
               return @App.sendContent res, result 
module.exports = Review


#U cannot do that?
