'use strict';

angular.module('myApp.domains', ['ngResource', 'toaster'])

  .controller('DomainCtrl', [
    '$scope', '$http', 'toaster', 'Domain',
    function($scope, $http, toaster, Domain) {
      $scope.createDomain = function createDomain() {
        $scope.newDomain.$save(function handleSuccess(object, responseHeaders) {
          resetNew();
          updateList();
        }, function handleError(response) {
          toaster.pop('error', response.data.message || 'Could not save domain');
        });
      };

      $scope.deleteDomain = function deleteDomain(domain) {
        domain.$delete(function handleSuccess(object, responseHeaders) {
          updateList();
        }, function handleError(response) {
          toaster.pop('error', response.data.message || 'Could not delete domain');
        });
      };

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
      return $resource('api/domains/:id', {id: '@_id'});
    }]);
