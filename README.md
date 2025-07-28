# SecretSolver

This Java program reconstructs the original secret from a set of shares using Shamir's Secret Sharing scheme. It reads input from a JSON file with arbitrary bases (binary, octal, decimal, hex, etc.), evaluates the values, applies Lagrange interpolation on all k-combinations of shares, and outputs the most probable original secret while eliminating incorrect shares.

📦 Features
Parses dynamic JSON input (n, k, and shares in various bases)

Evaluates share expressions to base-10 BigIntegers

Tries all combinations of k shares from n

Reconstructs (k-1)-degree polynomial using Lagrange interpolation

Identifies the most frequently reconstructed secret

Detects and eliminates incorrect shares

Handles arbitrarily large numbers

# WorkFlow

<img width="500" height="500" alt="ChatGPT Image Jul 28, 2025, 11_37_35 AM" src="https://github.com/user-attachments/assets/55321726-cd6d-4c39-bc92-9c11e217e2fe" />
