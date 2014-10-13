'use strict';

angular.module('myApp.tokens', ['ngResource', 'toaster'])

  .controller('TokenCtrl', [
    '$scope', '$http', 'toaster', 'Token',
    function($scope, $http, toaster, Token) {
      $scope.createToken = function() {
        $scope.newToken.$save();
        reset();
      };

      function reset() {
        $scope.newToken = new Token();
        $scope.tokenList = Token.query();
      }

      reset();
    }])

  .factory('Token', [
    '$resource',
    function($resource) {
      return $resource('api/tokens/:id', {id: '@_id'});
    }]);
