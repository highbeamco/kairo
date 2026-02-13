package kairo.dependencyInjection

import org.koin.core.module.Module

/** Implement this interface on Features that provide Koin dependency injection modules. */
public interface HasKoinModules {
  /** The Koin modules this Feature contributes. Scanned automatically during DI Feature startup. */
  public val koinModules: List<Module>
}
