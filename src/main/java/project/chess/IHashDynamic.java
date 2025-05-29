/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hashing;

import java.lang.String;

public interface IHashDynamic {
    int hash(int key);
    void add(int Key, String Value);
    void delete(int Key);
    String item(int Key);
    boolean contains(int Key);
    int length();
    boolean isEmpty();    
}
