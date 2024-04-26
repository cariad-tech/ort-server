/* prettier-ignore-start */

/* eslint-disable */

// @ts-nocheck

// noinspection JSUnusedGlobalSymbols

// This file is auto-generated by TanStack Router

// Import Routes

import { Route as rootRoute } from './routes/__root'
import { Route as LayoutRouteImport } from './routes/_layout/route'
import { Route as LayoutIndexImport } from './routes/_layout/index'
import { Route as LayoutCreateOrganizationImport } from './routes/_layout/create-organization'
import { Route as LayoutOrganizationsOrgIdRouteImport } from './routes/_layout/organizations/$orgId.route'
import { Route as LayoutOrganizationsOrgIdIndexImport } from './routes/_layout/organizations/$orgId.index'
import { Route as LayoutOrganizationsOrgIdEditImport } from './routes/_layout/organizations/$orgId.edit'
import { Route as LayoutOrganizationsOrgIdCreateProductImport } from './routes/_layout/organizations/$orgId.create-product'
import { Route as LayoutOrganizationsOrgIdProductsProductIdRouteImport } from './routes/_layout/organizations/$orgId.products.$productId.route'
import { Route as LayoutOrganizationsOrgIdProductsProductIdIndexImport } from './routes/_layout/organizations/$orgId.products.$productId.index'
import { Route as LayoutOrganizationsOrgIdProductsProductIdEditImport } from './routes/_layout/organizations/$orgId.products.$productId.edit'

// Create/Update Routes

const LayoutRouteRoute = LayoutRouteImport.update({
  id: '/_layout',
  getParentRoute: () => rootRoute,
} as any)

const LayoutIndexRoute = LayoutIndexImport.update({
  path: '/',
  getParentRoute: () => LayoutRouteRoute,
} as any)

const LayoutCreateOrganizationRoute = LayoutCreateOrganizationImport.update({
  path: '/create-organization',
  getParentRoute: () => LayoutRouteRoute,
} as any)

const LayoutOrganizationsOrgIdRouteRoute =
  LayoutOrganizationsOrgIdRouteImport.update({
    path: '/organizations/$orgId',
    getParentRoute: () => LayoutRouteRoute,
  } as any)

const LayoutOrganizationsOrgIdIndexRoute =
  LayoutOrganizationsOrgIdIndexImport.update({
    path: '/',
    getParentRoute: () => LayoutOrganizationsOrgIdRouteRoute,
  } as any)

const LayoutOrganizationsOrgIdEditRoute =
  LayoutOrganizationsOrgIdEditImport.update({
    path: '/edit',
    getParentRoute: () => LayoutOrganizationsOrgIdRouteRoute,
  } as any)

const LayoutOrganizationsOrgIdCreateProductRoute =
  LayoutOrganizationsOrgIdCreateProductImport.update({
    path: '/create-product',
    getParentRoute: () => LayoutOrganizationsOrgIdRouteRoute,
  } as any)

const LayoutOrganizationsOrgIdProductsProductIdRouteRoute =
  LayoutOrganizationsOrgIdProductsProductIdRouteImport.update({
    path: '/products/$productId',
    getParentRoute: () => LayoutOrganizationsOrgIdRouteRoute,
  } as any)

const LayoutOrganizationsOrgIdProductsProductIdIndexRoute =
  LayoutOrganizationsOrgIdProductsProductIdIndexImport.update({
    path: '/',
    getParentRoute: () => LayoutOrganizationsOrgIdProductsProductIdRouteRoute,
  } as any)

const LayoutOrganizationsOrgIdProductsProductIdEditRoute =
  LayoutOrganizationsOrgIdProductsProductIdEditImport.update({
    path: '/edit',
    getParentRoute: () => LayoutOrganizationsOrgIdProductsProductIdRouteRoute,
  } as any)

// Populate the FileRoutesByPath interface

declare module '@tanstack/react-router' {
  interface FileRoutesByPath {
    '/_layout': {
      preLoaderRoute: typeof LayoutRouteImport
      parentRoute: typeof rootRoute
    }
    '/_layout/create-organization': {
      preLoaderRoute: typeof LayoutCreateOrganizationImport
      parentRoute: typeof LayoutRouteImport
    }
    '/_layout/': {
      preLoaderRoute: typeof LayoutIndexImport
      parentRoute: typeof LayoutRouteImport
    }
    '/_layout/organizations/$orgId': {
      preLoaderRoute: typeof LayoutOrganizationsOrgIdRouteImport
      parentRoute: typeof LayoutRouteImport
    }
    '/_layout/organizations/$orgId/create-product': {
      preLoaderRoute: typeof LayoutOrganizationsOrgIdCreateProductImport
      parentRoute: typeof LayoutOrganizationsOrgIdRouteImport
    }
    '/_layout/organizations/$orgId/edit': {
      preLoaderRoute: typeof LayoutOrganizationsOrgIdEditImport
      parentRoute: typeof LayoutOrganizationsOrgIdRouteImport
    }
    '/_layout/organizations/$orgId/': {
      preLoaderRoute: typeof LayoutOrganizationsOrgIdIndexImport
      parentRoute: typeof LayoutOrganizationsOrgIdRouteImport
    }
    '/_layout/organizations/$orgId/products/$productId': {
      preLoaderRoute: typeof LayoutOrganizationsOrgIdProductsProductIdRouteImport
      parentRoute: typeof LayoutOrganizationsOrgIdRouteImport
    }
    '/_layout/organizations/$orgId/products/$productId/edit': {
      preLoaderRoute: typeof LayoutOrganizationsOrgIdProductsProductIdEditImport
      parentRoute: typeof LayoutOrganizationsOrgIdProductsProductIdRouteImport
    }
    '/_layout/organizations/$orgId/products/$productId/': {
      preLoaderRoute: typeof LayoutOrganizationsOrgIdProductsProductIdIndexImport
      parentRoute: typeof LayoutOrganizationsOrgIdProductsProductIdRouteImport
    }
  }
}

// Create and export the route tree

export const routeTree = rootRoute.addChildren([
  LayoutRouteRoute.addChildren([
    LayoutCreateOrganizationRoute,
    LayoutIndexRoute,
    LayoutOrganizationsOrgIdRouteRoute.addChildren([
      LayoutOrganizationsOrgIdCreateProductRoute,
      LayoutOrganizationsOrgIdEditRoute,
      LayoutOrganizationsOrgIdIndexRoute,
      LayoutOrganizationsOrgIdProductsProductIdRouteRoute.addChildren([
        LayoutOrganizationsOrgIdProductsProductIdEditRoute,
        LayoutOrganizationsOrgIdProductsProductIdIndexRoute,
      ]),
    ]),
  ]),
])

/* prettier-ignore-end */
