/*
 * generated by Xtext 2.14.0
 */
package ldas.xtext.ide

import com.google.inject.Guice
import ldas.xtext.DucRuntimeModule
import ldas.xtext.DucStandaloneSetup
import org.eclipse.xtext.util.Modules2

/**
 * Initialization support for running Xtext languages as language servers.
 */
class DucIdeSetup extends DucStandaloneSetup {

	override createInjector() {
		Guice.createInjector(Modules2.mixin(new DucRuntimeModule, new DucIdeModule))
	}
	
}