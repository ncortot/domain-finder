'use strict';

angular.module('myApp.tokens', ['ngResource', 'toaster'])

  .controller('TokenCtrl', [
    '$scope', '$http', 'toaster', 'Token',
    function($scope, $http, toaster, Token) {
      $scope.createToken = function createToken() {
        $scope.newToken.$save(function(object, responseHeaders) {
          resetNew();
          updateList();
        }, function(response) {
          toaster.pop('error', response.data.message || 'Could not save token');
        });
      };

      $scope.deleteToken = function(token) {
        token.$delete(function(object, responseHeaders) {
          updateList();
        }, function(response) {
          toaster.pop('error', response.data.message || 'Could not delete token');
        });
      };

      $scope.generateDomains = function() {
        $http.post('/api/domains/generate', {
        }, function(object, responseHeaders) {
          toaster.pop('success', response.data.message || 'Domain list updated');
        }, function(response) {
          toaster.pop('error', response.data.message || 'Could not generate domains');
        });
      };

      function resetNew() {
        $scope.newToken = new Token();
      }

      function updateList() {
        var tokenList = Token.query(function() {
          $scope.tokenList = tokenList;
        });
      }

      resetNew();
      updateList();
    }])

  .factory('Token', [
    '$resource',
    function($resource) {
      return $resource('/api/tokens/:id', {id: '@_id'});
    }]);
