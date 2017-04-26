// BASE SETUP
// =============================================================================

// call the packages we need
var express    = require('express');        // call express
var app        = express();                 // define our app using express
var bodyParser = require('body-parser');
var request    = require('request');
var cors       = require('cors');
// configure app to use bodyParser()
// this will let us get the data from a POST
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port = process.env.PORT || 4500;        // set our port

// ROUTES FOR OUR API
// =============================================================================
var router = express.Router();              // get an instance of the express Router
app.use(cors({origin: 'http://localhost:4200'}));
app.post('/ratios', function(req, res) {
	var options = {
		url: 'http://edge.platform.cooksys.com:8081/routes/airlines/ratios',
		method: 'POST',
		headers: {
			'Content-type': 'application/json'
		},
		json: req.body
	};
	console.log(req.body);
	request(options, function(err, res, body) {
		if (res && (res.statusCode === 200 || res.statusCode === 201)) {
			console.log(body);
		}
	});
	res.send('Success.');
});

app.use('/', router);
// START THE SERVER
// =============================================================================
app.listen(port);
console.log('Magic happens on port ' + port);
