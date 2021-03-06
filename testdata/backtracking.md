-   Backtracking incrementally builds candidates to the solutions, and
    abandon each partial candidate as soon as it cannot possibly be
    completed to a valid solution.
-   Backtracking can be applied only for problems which admit the
    concept of a **partial candidate solution** and a relatively quick
    test of whether it can possibly be completed to a valid solution.
-   When it applicable, it often much faster than brute force
    enumeration of all complete candidates, since it eliminate a large
    number of candidates with a single test.

# Description of the method

-   Conceptually, the partial candidates are represented as the **nodes** of
    a **tree** structure, *the potential search tree*.
-   Each partial candidate is the parent of the candidates that *differ
    from it by a **single extension step***;
-   The **leaves** of the tree are the partial candidates that cannot be
    extended any further, i.e, a possible solution.
-   The backtracking algorithm traverses this search tree recursively,
    from the root down, in **depth-first** order.

# The algorithm:

-   [The tutorial](https://www.cis.upenn.edu/~matuszek/cit594-2012/Pages/backtracking.html)
-   Recursive code:
    
        boolean solve(Node n) {
          if (is_leaf(n)) {
            if (is_goal(n)) {
              return true;
            }
            else return false;
          } else {
            for (c : child(n)) {
                if ( solve(c) ) return true;
            }
            return false;
          }
        }
    
    The reasoning:
    
    -   If any of the children of n is solvable, n is solvable
    -   If non of the children of n is solvable, n is non-solvable
