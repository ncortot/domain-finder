'use strict';

angular.module('myApp.tokens', ['ngResource', 'toaster'])

  .controller('TokenCtrl', [
    '$scope', '$http', 'toaster', 'Token',
    function($scope, $http, toaster, Token) {
      $scope.createToken = function createToken() {
        $scope.newToken.$save(function handleSuccess(object, responseHeaders) {
          resetNew();
          updateList();
        }, function handleError(response) {
          toaster.pop('error', response.data.message || 'Could not save token');
        });
      };

      $scope.deleteToken = function deleteToken(token) {
        token.$delete(function handleSuccess(object, responseHeaders) {
          updateList();
        }, function handleError(response) {
          toaster.pop('error', response.data.message || 'Could not delete token');
        });
      };

      function resetNew() {
        $scope.newToken = new Token();
      }

      function updateList() {
        $scope.tokenList = Token.query();
      }

      resetNew();
      updateList();
    }])

  .factory('Token', [
    '$resource',
    function($resource) {
      return $resource('api/tokens/:id', {id: '@_id'});
    }]);
