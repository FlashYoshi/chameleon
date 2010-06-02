package chameleon.oo.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.generics.ActualTypeArgument;
import chameleon.oo.type.generics.InstantiatedTypeParameter;
import chameleon.oo.type.generics.TypeParameter;
import chameleon.util.Pair;


/**
 * A derived type is created by filling in the type parameters of a parameterized
 * type.
 * 
 * @author Marko van Dooren
 */
public class DerivedType extends TypeWithBody {
	
	public DerivedType(List<TypeParameter> typeParameters, Type baseType) {
		this(baseType);
		substituteParameters(typeParameters);
	}

	/**
	 * Create a new derived type for the given base type.
	 * The contents of the type is copied into this type.
	 * @param baseType
	 */
	private DerivedType(Type baseType) {
		super(baseType.signature().clone());
		copyContents(baseType, true);
		_baseType = baseType;
		setOrigin(baseType);
	}
	
	public static DerivedType create(Type baseType, List<ActualTypeArgument> typeArguments) throws LookupException {
		Map<List<Type>, DerivedType> map = _cache.get(baseType);
		if(map == null) {
			map = new HashMap<List<Type>, DerivedType>();
			_cache.put(baseType, map);
		} 
//		else {
//			MAPCOUNT++;
//			System.out.println("Map cache hits: "+MAPCOUNT);
//		}
		List<Type> typeList = new ArrayList<Type>();
		for(ActualTypeArgument arg: typeArguments) {
			typeList.add(arg.type());
		}
		DerivedType result = map.get(typeList);
		if(result == null) {
			result = new DerivedType(baseType, typeArguments);
			map.put(typeList, result);
		} else {
			COUNT++;
			System.out.println("Cache hits: "+COUNT);
		}
		return result;
	}
	
	private static int COUNT;
	
	private static Map<Type, Map<List<Type>, DerivedType>> _cache = new HashMap<Type, Map<List<Type>, DerivedType>>();
	
	/**
	 * Create a derived type by filling in the type parameters with the given list of
	 * actual type arguments.
	 * 
	 * Note that the arguments are reversed compared to the constructor which takes a list
	 * of type parameters because otherwise, both constructors would have the same erasure.
	 * 
	 * @param baseType
	 * @param typeArguments
	 */
	public DerivedType(Type baseType, List<ActualTypeArgument> typeArguments) {
		this(baseType);
		// substitute parameters
		List<TypeParameter> myParameters = parameters();
		Iterator<TypeParameter> parametersIterator = myParameters.iterator();
		Iterator<ActualTypeArgument> argumentsIterator = typeArguments.iterator();
		while (parametersIterator.hasNext()) {
			TypeParameter parameter = parametersIterator.next();
			ActualTypeArgument argument = argumentsIterator.next();
			// The next call does not change the parent of 'argument'. It is stored in InstantiatedTypeParameter
			// using a regular reference.
			InstantiatedTypeParameter instantiated = new InstantiatedTypeParameter(parameter.signature().clone(), argument);
			replaceParameter(parameter, instantiated);
		}

	}
	
	@Override
	public boolean uniSameAs(Element otherType) throws LookupException {
		boolean result = false;
		if(otherType instanceof DerivedType) {
			DerivedType type = (DerivedType) otherType;
			result = type.baseType().sameAs(baseType());
			Iterator<TypeParameter> myParams = parameters().iterator();
			Iterator<TypeParameter> otherParams = type.parameters().iterator();
			while(myParams.hasNext() && result) {
				TypeParameter mine = myParams.next();
				TypeParameter otherParam = otherParams.next();
				result = mine.sameValueAs(otherParam, new ArrayList());
			}
		}
		return result;
	}
	
	public boolean uniSameAs(Type otherType, List<Pair<TypeParameter, TypeParameter>> trace) throws LookupException {
		boolean result = false;
		if(otherType instanceof DerivedType) {
			DerivedType type = (DerivedType) otherType;
			result = type.baseType().sameAs(baseType());
			Iterator<TypeParameter> myParams = parameters().iterator();
			Iterator<TypeParameter> otherParams = type.parameters().iterator();
			while(myParams.hasNext() && result) {
				TypeParameter mine = myParams.next();
				TypeParameter otherParam = otherParams.next();
				result = mine.sameValueAs(otherParam,trace);
			}
		}
		return result;
	}

	
	@Override
	public int hashCode() {
		int result = baseType().hashCode();
//		for(TypeParameter parameter: parameters()) {
//			result += parameter.hashCode();
//		}
		return result;
	}
	
	private Type _baseType;
	
	@Override
	public Type baseType() {
		return _baseType;
	}

	@Override
	public DerivedType clone() {
		List<TypeParameter> args = new ArrayList<TypeParameter>();
		for(TypeParameter par: parameters()) {
			args.add(par.clone());
		}
		return new DerivedType(args,baseType());
	}

}
