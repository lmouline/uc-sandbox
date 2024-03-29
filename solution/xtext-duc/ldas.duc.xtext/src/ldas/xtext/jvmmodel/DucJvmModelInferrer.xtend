/*
 * generated by Xtext 2.14.0
 */
package ldas.xtext.jvmmodel

import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import fr.inria.diverse.xcore.lang.jvmmodel.LxcoreJvmModelInferrer
import javax.inject.Inject
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

//import ldas.xtext.duc.Function
//import ldas.xtext.duc.Statement

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>     
 */
class DucJvmModelInferrer extends LxcoreJvmModelInferrer {

	/**
	 * convenience API to build and initialize JVM types and their members.
	 */
	@Inject extension JvmTypesBuilder

	/**
	 * The dispatch method {@code infer} is called for each instance of the
	 * given element's type that is contained in a resource.
	 * 
	 * @param element
	 *            the model to create one or more
	 *            {@link org.eclipse.xtext.common.types.JvmDeclaredType declared
	 *            types} from.
	 * @param acceptor
	 *            each created
	 *            {@link org.eclipse.xtext.common.types.JvmDeclaredType type}
	 *            without a container should be passed to the acceptor in order
	 *            get attached to the current resource. The acceptor's
	 *            {@link IJvmDeclaredTypeAcceptor#accept(org.eclipse.xtext.common.types.JvmDeclaredType)
	 *            accept(..)} method takes the constructed empty type for the
	 *            pre-indexing phase. This one is further initialized in the
	 *            indexing phase using the lambda you pass as the last argument.
	 * @param isPreIndexingPhase
	 *            whether the method is called in a pre-indexing phase, i.e.
	 *            when the global index is not yet fully updated. You must not
	 *            rely on linking using the index if isPreIndexingPhase is
	 *            <code>true</code>.
	 */
//	 def dispatch void infer(Package ducPack, IJvmDeclaredTypeAcceptor acceptor, boolean isPreIndexingPhase) {
//	 	infer(ducPack.papa, acceptor, isPreIndexingPhase)
//	 }
//	def dispatch void infer(File ducFile, IJvmDeclaredTypeAcceptor acceptor, boolean isPreIndexingPhase) {
//		for(Statement stmt: ducFile.statements) {
////			infer(stmt, acceptor, isPreIndexingPhase)
//		}
//	}
//	
//	def dispatch void infer(ldas.xtext.duc.Class ducClass, IJvmDeclaredTypeAcceptor acceptor, boolean isPreIndexingPhase) {
//		
//	}
//	
//	def dispatch void infer(Function ducFct, IJvmDeclaredTypeAcceptor acceptor, boolean isPreIndexingPhase) {
//		
//	}
}
