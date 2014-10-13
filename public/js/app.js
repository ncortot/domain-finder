/* global angular */

'use strict';

// Declare app level module which depends on filters, and services
angular.module('myApp', [
  'ngCookies',
  'ngRoute',
  'myApp.domains',
  'myApp.tokens',
  'myApp.util'
]).config(['$locationProvider', function($locationProvider) {
  $locationProvider.html5Mode(true).hashPrefix('!');
}]).config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/domains', {
    templateUrl: '/assets/partials/domains.html',
    controller: 'DomainCtrl'
  });
  $routeProvider.when('/tokens', {
    templateUrl: '/assets/partials/tokens.html',
    controller: 'TokenCtrl'
  });
  $routeProvider.otherwise({redirectTo: '/domains'});
}]).run(['$http', '$cookies', function($http, $cookies) {
  $http.defaults.headers.common['X-CSRFToken'] = $cookies.csrftoken;
}]);
