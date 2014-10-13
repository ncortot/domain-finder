'use strict';

angular.module('myApp.domains', ['ngResource', 'toaster'])

  .controller('DomainCtrl', [
    '$scope', '$http', 'toaster',
    function($scope, $http, toaster) {

    }])

  .factory('Domain', [
    '$resource',
    function($resource) {
      return $resource('api/domains/:id', {id: '@_id'});
    }]);
