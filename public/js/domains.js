'use strict';

angular.module('myApp.domains', ['ngResource', 'toaster'])

  .controller('DomainCtrl', [
    '$scope', '$http', 'toaster', 'Domain',
    function($scope, $http, toaster, Domain) {
      $scope.filters = {
        available: true,
        score: true
      }

      $scope.filterDomain = function(domain) {
        if ($scope.filters.available && (!domain.available && !domain.owned))
          return false;
        if ($scope.filters.score && domain.score < 0)
          return false;
        return true;
      }

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

      $scope.validateDomains = function() {
        $http.post('/api/domains/validate', {
        }, function(object, responseHeaders) {
          toaster.pop('success', response.data.message || 'Domain list validated');
        }, function(response) {
          toaster.pop('error', response.data.message || 'Could not validate domains');
        });
      };

      function resetNew() {
        $scope.newDomain = new Domain();
        $scope.newDomain.score = 0;
      }

      function updateList() {
        var domainList = Domain.query(function() {
          $scope.domainList = domainList;
        });
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
