var app = angular.module('odyssey', ['ngRoute']);


app.config(function ($routeProvider) {
    $routeProvider
        .when('/dashboard', {
            templateUrl: '../pages/dashboard.html'
        })
        .when('/mongodb', {
            templateUrl: '../pages/mongodb.html',
            controller: 'ProfileCtrl'
        })
        .when('/mongodash', {
            templateUrl: '../pages/mongodashboard.html',
            controller: 'ProfileCtrl'
        })
        .when('/sqlserver', {
            templateUrl: '../pages/sqlserver.html',
            controller: 'ProfileCtrl'
        })
        .when('/sqlquery', {
            templateUrl: '../pages/query.html',
            controller: 'ProfileCtrl'
        })
        .otherwise({
            redirectTo: '/dashboard'
        })
});



app.controller('ProfileCtrl', function ($routeParams, $http, $scope) {
  
});

app.controller('LibraryCtrl', function ($routeParams, $http, $scope) {

});


app.controller('SongCtrl', function ($routeParams, $http, $scope) {

});


app.controller('LyricsCtrl', function ($http, $scope) {
  
});

