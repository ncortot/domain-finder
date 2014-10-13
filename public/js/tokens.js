'use strict';

angular.module('myApp.tokens', ['ngResource', 'toaster'])

  .controller('TokenCtrl', [
    '$scope', '$http', 'toaster',
    function($scope, $http, toaster) {

    }])

  .factory('Token', [
    '$resource',
    function($resource) {
      return $resource('api/tokens/:id', {id: '@_id'});
    }]);
