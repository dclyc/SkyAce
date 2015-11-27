class Entity
	constructor : (@App)->
		console.log "hello"
		@App.router.get "/entities/create", @createEntity
		@App.router.get "/entities" , @listEntities
		console.log @createEntity
	createEntity : (req, res)=>
		@App.Models.entities.insert { name : "Wei Cong"}, (err, result)=>
			if !err
				return @App.sendContent res, result
			else
				return @App.sendErrorCode res, 500, err
	listEntities : (req, res)=>
		@App.Models.entities.find({}).toArray (err, results)=>
			if !err
				return @App.sendContent res, results
			else
				return @App.sendErrorCode res, 500, err
module.exports = Entity