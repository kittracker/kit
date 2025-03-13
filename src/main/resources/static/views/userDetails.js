export default async function userDetails({ id }) {
    const response = await fetch(`/api/users/${id}`);
    if (!response.ok) {
        return `
            <div class="text-center">
                <h2>Issue Not Found</h2>
            </div>
        `;
    }

    const user = await response.json();

    return `
        <div class="container mt-5">
            <h1 class="mb-4">User Details</h1>
            <div class="card p-4">
                <h2>@${user.username}</h2>
                <p>Email: ${user.emailAddress || "N/A"}</p>
                <a href="/users" data-link class="btn btn-primary">Back to Users</a>
            </div>
        </div>
    `;
}
