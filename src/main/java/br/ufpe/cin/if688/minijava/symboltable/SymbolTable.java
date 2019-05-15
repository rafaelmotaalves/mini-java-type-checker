package br.ufpe.cin.if688.minijava.symboltable;

import java.util.Hashtable;

import br.ufpe.cin.if688.minijava.ast.*;

public class SymbolTable {

	private Hashtable<Object, Object> symbolTable;

	public SymbolTable() {
		symbolTable = new Hashtable<Object, Object>();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Object key: symbolTable.keySet()) {
			Class _class = (Class) symbolTable.get(key);
			sb.append(_class.toString());
		}

		return sb.toString();
	}


	public boolean addClass(String id, String parent) {
		if (containsClass(id)) {
			return false;
		}
		else {
			symbolTable.put(id, new Class(id, parent));
		}
		return true;
	}

	public Class getClass(String id) {
		if (containsClass(id))
			return (Class) symbolTable.get(id);
		else
			return null;
	}

	public boolean containsClass(String id) {
		return symbolTable.containsKey(id);
	}

	public Type getVarType(Method m, Class c, String id) {
		if (m != null) {
			if (m.getVar(id) != null) {
				return m.getVar(id).type();
			}
			if (m.getParam(id) != null) {
				return m.getParam(id).type();
			}
		}

		while (c != null) {
			if (c.getVar(id) != null) {
				return c.getVar(id).type();
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getClass(c.parent());
				}
			}
		}

		throw new RuntimeException(String.format("Variavel " + id + " nao definida no escopo atual"));
	}

	public Method getMethod(String id, String classScope) {
		if (getClass(classScope) == null) {
			throw new RuntimeException(String.format("Class " + classScope + " nao definida"));
		}

		Class c = getClass(classScope);
		while (c != null) {
			if (c.getMethod(id) != null) {
				return c.getMethod(id);
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getClass(c.parent());
				}
			}
		}

		throw new RuntimeException(String.format("Metodo " + id + " nao definido na classe " + classScope));

	}

	public Type getMethodType(String id, String classScope) {
		if (getClass(classScope) == null) {
			throw new RuntimeException(String.format("Classe " + classScope + " nao definida"));
		}

		Class c = getClass(classScope);
		while (c != null) {
			if (c.getMethod(id) != null) {
				return c.getMethod(id).type();
			} else {
				if (c.parent() == null) {
					c = null;
				} else {
					c = getClass(c.parent());
				}
			}
		}

		throw new RuntimeException(String.format("Metodo " + id + " nao definido na classe " + classScope));
	}

	public boolean compareTypes(Type t1, Type t2) {

		if (t1 == null || t2 == null)
			return false;

		if (t1 instanceof IntegerType && t2 instanceof IntegerType)
			return true;
		if (t1 instanceof BooleanType && t2 instanceof BooleanType)
			return true;
		if (t1 instanceof IntArrayType && t2 instanceof IntArrayType)
			return true;
		if (t1 instanceof IdentifierType && t2 instanceof IdentifierType) {
			IdentifierType i1 = (IdentifierType) t1;
			IdentifierType i2 = (IdentifierType) t2;

			Class c = getClass(i2.s);
			while (c != null) {
				if (i1.s.equals(c.getId()))
					return true;
				else {
					if (c.parent() == null)
						return false;
					c = getClass(c.parent());
				}
			}
		}
		return false;
	}

}// SymbolTable