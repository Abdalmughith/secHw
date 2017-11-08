var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var _ = require('lodash');
var clientes = [];

server.listen(3000);
app.get('/', function(req, res) {
	res.sendFile(__dirname + '/index.html');
});
app.get('/c2', function(req, res) {
	res.sendFile(__dirname + '/c2.html');
})

app.get('/clientes', function(req, res) {
	res.json(clientes)
})
	// body...

// io.on('connection', function (socket) {
// 	socket.emit('sendToMeYourNameAndPublicKey')
// 	console.log("socket.id", socket.id);

// 	socket.on("myNickNameAndPublicKey",function (data) {
// 	console.log(data)
// 	})

// });


var users = [];
io.on('connection', (socket) => {
			socket.on('username', (userName) => {
				console.log("userName has join", userName);

		      	users.push({
		      		id : socket.id,
		      		userName : userName
		      	});

		      	var len = users.length;
		      	len--;

		      	 io.emit('userList', users, users[len].id); 
		    });

		    socket.on('getMsg', (data) => {
		    	socket.broadcast.to(data.toid).emit('sendMsg',{
		    		msg:data.msg,
		    		name:data.name
		    	});
		    });

		    socket.on('disconnect',()=>{
		    	
		      	for(var i=0; i <  users.length; i++){
		        	
		        	if( users[i].id === socket.id){
		          		 users.splice(i,1); 
		        	}
		      	}
		      	 io.emit('exit', users); 
		    });

});