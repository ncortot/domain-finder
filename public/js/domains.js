'use strict';

angular.module('myApp.domains', ['ngResource', 'toaster'])

  .controller('DomainCtrl', [
    '$scope', '$http', 'toaster', 'Domain',
    function($scope, $http, toaster, Domain) {
      $scope.createDomain = function() {
        $scope.newDomain.$save(function(object, responseHeaders) {
          resetNew();
          updateList();
        }, function(response) {
          toaster.pop('error', response.data.message || 'Could not save domain');
        });
      };

      $scope.deleteDomain = function(domain) {
        domain.$delete(function(object, responseHeaders) {
          updateList();
        }, function(response) {
          toaster.pop('error', response.data.message || 'Could not delete domain');
        });
      };

      $scope.updateFlag = function(domain, flag, state) {
        domain.$updateFlag({
          flag: flag,
          state: state
        }, function(object, responseHeaders) {
          updateList();
        }, function(response) {
          toaster.pop('error', response.data.message || 'Could not update domain');
        });
      }

      $scope.updateScore = function(domain, delta) {
        domain.$updateScore({
          delta: delta
        }, function(object, responseHeaders) {
          updateList();
        }, function(response) {
          toaster.pop('error', response.data.message || 'Could not update domain');
        });
      }

      function resetNew() {
        $scope.newDomain = new Domain();
        $scope.newDomain.score = 0;
      }

      function updateList() {
        $scope.domainList = Domain.query();
      }

      resetNew();
      updateList();
    }])

  .factory('Domain', [
    '$resource',
    function($resource) {
      return $resource('api/domains/:id', {id: '@_id'}, {
        updateFlag: {
          method: 'POST',
          url: 'api/domains/:id/:flag/:state'
        },
        updateScore: {
          method: 'POST',
          url: 'api/domains/:id/score/:delta'
        }
      });
    }]);
