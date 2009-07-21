package org.eclipse.cdt.objc.core.internal.parser.scanner;

class TokenList {
    private Token fFirst;
    private Token fLast;

    public final void append(Token t) {
        if (fFirst == null) {
            fFirst = fLast = t;
        } else {
            fLast.setNext(t);
            fLast = t;
        }
        t.setNext(null);
    }

    public final void appendAll(TokenList tl) {
        final Token t = tl.first();
        if (t != null) {
            if (fFirst == null) {
                fFirst = tl.fFirst;
            } else {
                fLast.setNext(tl.fFirst);
            }
            fLast = tl.fLast;
        }
        tl.fFirst = tl.fLast = null;
    }

    public final void appendAllButLast(TokenList tl) {
        Token t = tl.first();
        if (t != null) {
            for (Token n = (Token) t.getNext(); n != null; t = n, n = (Token) n.getNext()) {
                append(t);
            }
        }
    }

    public void clear() {
        fFirst = fLast = null;
    }

    public final TokenList cloneTokens() {
        TokenList result = new TokenList();
        for (Token t = fFirst; t != null; t = (Token) t.getNext()) {
            if (t.getType() != ObjCPreprocessor.tSCOPE_MARKER) {
                result.append((Token) t.clone());
            }
        }
        return result;
    }

    void cutAfter(Token l) {
        if (l == null) {
            fFirst = fLast = null;
        } else {
            l.setNext(null);
            fLast = l;
        }
    }

    public final Token first() {
        return fFirst;
    }

    public boolean isEmpty() {
        return fFirst == null;
    }

    public final Token last() {
        return fLast;
    }

    public final void prepend(TokenList prepend) {
        final Token first = prepend.fFirst;
        if (first != null) {
            final Token last = prepend.fLast;
            last.setNext(fFirst);
            fFirst = first;
            if (fLast == null) {
                fLast = last;
            }
        }
    }

    final void removeBehind(Token l) {
        if (l == null) {
            Token t = fFirst;
            if (t != null) {
                t = (Token) t.getNext();
                fFirst = t;
                if (t == null) {
                    fLast = null;
                }
            }
        } else {
            final Token r = (Token) l.getNext();
            if (r != null) {
                l.setNext(r.getNext());
                if (r == fLast) {
                    fLast = l;
                }
            }
        }
    }

    final Token removeFirst() {
        final Token first = fFirst;
        if (first == fLast) {
            fFirst = fLast = null;
            return first;
        }
        fFirst = (Token) first.getNext();
        return first;
    }
}
