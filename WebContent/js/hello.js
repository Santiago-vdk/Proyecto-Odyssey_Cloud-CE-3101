var app = angular.module('odyssey', [ 'ngRoute' ]);

app.config(function($routeProvider) {
	$routeProvider.when('/dashboard', {
		templateUrl : '../pages/dashboard.html'
	}).when('/mongodb', {
		templateUrl : '../pages/mongodb.html',
		controller : 'ProfileCtrl'
	}).when('/mongodash', {
		templateUrl : '../pages/mongodashboard.html',
		controller : 'ProfileCtrl'
	}).when('/sqlserver', {
		templateUrl : '../pages/sqlserver.html',
		controller : 'ProfileCtrl'
	}).when('/sqlquery/:selected', {
		templateUrl : '../pages/query.html',
		controller : 'QueryCtrl'
	}).when('/profile/:userid', {
		templateUrl : '../pages/profile.html',
		controller : 'ProfileCtrl'
	}).when('/tables', {
		templateUrl : '../pages/tables.html',
		controller : 'TablesCtrl'
	}).otherwise({
		redirectTo : '/dashboard'
	})
});

app.controller('TablesCtrl', function($routeParams, $http, $scope) {

});

app.controller('ProfileCtrl', function($routeParams, $http, $scope) {
	$scope.profileid = $routeParams.userid;
	sessionStorage["tempUSER"] = String($scope.profileid);
	
	$scope.playTrack = function(songid){
	       var endpoint = 'http://192.168.1.135:9080/OdysseyCloud/' + 'api/v1/users/' + $scope.profileid + '/libraries/1/songs/' + songid + '/stream/?for=' + sessionStorage.username;

	        console.log("Hearing stream, " + endpoint);
	        var audio = document.getElementById('music-player');


	        //audio.setAttribute('data',"Playing song with ID: " + $scope.songid);

	        audio.setAttribute('src', endpoint);
	        audio.pause();
	        audio.load(); //suspends and restores all audio element

	        audio.play();
	}
	
	  $http.get("http://192.168.1.135:9080/OdysseyCloud/" + 'api/v1/users/' + $scope.profileid + '/libraries/1/?type=lib').
	    success(function (data) {
	        $scope.data = data;
	    });
	  
});

app.controller('QueryCtrl', function($routeParams, $http, $scope) {
	$scope.table = $routeParams.selected;

	var postData = {
		"username" : sessionStorage.username,
		"token" : sessionStorage.token
	}
	
	 $http({
		 url : "http://192.168.1.135:9080/OdysseyCloud/"
				+ 'api/v1/tools/query/?query=' + $scope.table,
	        method: 'POST',
	        data: JSON.stringify(postData),
	        dataType: "json",
	        headers: {
	            'Content-Type': 'application/json'
	        }
	    }).success(function (data) {
	    	$scope.table = data;
	    	if (!$scope.$$phase) {
	            $scope.$apply();
	        }
	    });
});

app.controller('LovedCtrl', function($http, $scope) {
	var postData = {
			"username" : sessionStorage.username,
			"token" : sessionStorage.token
		}
	
	 $http({
		 url : "http://192.168.1.135:9080/OdysseyCloud/"
				+ 'api/v1/tools/social/?type=loved_users',
	        method: 'POST',
	        data: JSON.stringify(postData),
	        dataType: "json",
	        headers: {
	            'Content-Type': 'application/json'
	        }
	    }).success(function (data) {
	    	$scope.lovedUsers = data;
	    	if (!$scope.$$phase) {
	            $scope.$apply();
	        }
	    });
});

app.controller('HatedCtrl', function($http, $scope) {
	var postData = {
			"username" : sessionStorage.username,
			"token" : sessionStorage.token
		}
	
	 $http({
		 url : "http://192.168.1.135:9080/OdysseyCloud/"
				+ 'api/v1/tools/social/?type=hated_users',
	        method: 'POST',
	        data: JSON.stringify(postData),
	        dataType: "json",
	        headers: {
	            'Content-Type': 'application/json'
	        }
	    }).success(function (data) {
	    	$scope.hatedUsers = data;
	    	if (!$scope.$$phase) {
	            $scope.$apply();
	        }
	    });
});

app.controller('LovedSongsCtrl', function($http, $scope) {
	var postData = {
			"username" : sessionStorage.username,
			"token" : sessionStorage.token
		}
	
	 $http({
		 url : "http://192.168.1.135:9080/OdysseyCloud/"
				+ 'api/v1/tools/social/?type=loved_songs',
	        method: 'POST',
	        data: JSON.stringify(postData),
	        dataType: "json",
	        headers: {
	            'Content-Type': 'application/json'
	        }
	    }).success(function (data) {
	    	$scope.lovedSongs = data;
	    	if (!$scope.$$phase) {
	            $scope.$apply();
	        }
	    });
});

app.controller('HatedSongsCtrl', function($http, $scope) {
	var postData = {
			"username" : sessionStorage.username,
			"token" : sessionStorage.token
		}
	
	 $http({
		 url : "http://192.168.1.135:9080/OdysseyCloud/"
				+ 'api/v1/tools/social/?type=hated_songs',
	        method: 'POST',
	        data: JSON.stringify(postData),
	        dataType: "json",
	        headers: {
	            'Content-Type': 'application/json'
	        }
	    }).success(function (data) {
	    	$scope.hatedSongs = data;
	    	if (!$scope.$$phase) {
	            $scope.$apply();
	        }
	    });
});

app.controller('OnlineUsersCtrl', function($http, $scope) {
	var postData = {
			"username" : sessionStorage.username,
			"token" : sessionStorage.token
		}
	
	 $http({
		 url : "http://192.168.1.135:9080/OdysseyCloud/"
				+ 'api/v1/tools/social/?type=online_users',
	        method: 'POST',
	        data: JSON.stringify(postData),
	        dataType: "json",
	        headers: {
	            'Content-Type': 'application/json'
	        }
	    }).success(function (data) {
	    	$scope.onlineUsers = data;
	    	if (!$scope.$$phase) {
	            $scope.$apply();
	        }
	    });
});

app.controller('AllUsersCtrl', function($http, $scope) {
	var postData = {
			"username" : sessionStorage.username,
			"token" : sessionStorage.token
		}
	
	 $http({
		 url : "http://192.168.1.135:9080/OdysseyCloud/"
				+ 'api/v1/tools/social/?type=all_users',
	        method: 'POST',
	        data: JSON.stringify(postData),
	        dataType: "json",
	        headers: {
	            'Content-Type': 'application/json'
	        }
	    }).success(function (data) {
	    	$scope.allUsers = data;
	    	if (!$scope.$$phase) {
	            $scope.$apply();
	        }
	    });
});


app.controller('LibraryCtrl', function($routeParams, $http, $scope) {

});

app.controller('SongCtrl', function($routeParams, $http, $scope) {

});

app.controller('LyricsCtrl', function($http, $scope) {

});
